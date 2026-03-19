package su.afk.kemonos.profile.presenter.favoritePosts

import androidx.paging.cachedIn
import androidx.paging.filter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import su.afk.kemonos.creatorPost.api.ICreatorPostNavigator
import su.afk.kemonos.creatorProfile.api.ICreatorProfileNavigator
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.domain.models.PostDomain
import su.afk.kemonos.error.error.IErrorHandlerUseCase
import su.afk.kemonos.error.error.storage.RetryStorage
import su.afk.kemonos.navigation.NavigationManager
import su.afk.kemonos.navigation.storage.NavigationStorage
import su.afk.kemonos.preferences.site.ISelectedSiteUseCase
import su.afk.kemonos.preferences.site.setSiteAndAwait
import su.afk.kemonos.preferences.ui.IUiSettingUseCase
import su.afk.kemonos.profile.domain.favorites.GetFavoritePostsUseCase
import su.afk.kemonos.profile.domain.favorites.posts.GetFavoritePostsPagingUseCase
import su.afk.kemonos.profile.presenter.favoritePosts.FavoritePostsState.*
import su.afk.kemonos.profile.utils.Const.KEY_SELECT_SITE
import su.afk.kemonos.storage.api.repository.creators.IStoreCreatorsRepository
import su.afk.kemonos.storage.api.repository.favorites.post.IStoreFavoritePostsRepository
import su.afk.kemonos.ui.components.posts.filter.PostMediaFilter
import su.afk.kemonos.ui.components.posts.filter.matchesMediaFilter
import su.afk.kemonos.ui.presenter.baseViewModel.BaseViewModelNew
import javax.inject.Inject

@HiltViewModel
internal class FavoritePostsViewModel @Inject constructor(
    private val getFavoritePostsUseCase: GetFavoritePostsUseCase,
    private val storeCreatorsRepository: IStoreCreatorsRepository,
    private val storeFavoritePostsRepository: IStoreFavoritePostsRepository,
    private val navManager: NavigationManager,
    private val creatorProfileNavigator: ICreatorProfileNavigator,
    private val creatorPostNavigator: ICreatorPostNavigator,
    private val selectedSiteUseCase: ISelectedSiteUseCase,
    private val getFavoritePostsPagingUseCase: GetFavoritePostsPagingUseCase,
    private val navigationStorage: NavigationStorage,
    private val uiSetting: IUiSettingUseCase,
    override val errorHandler: IErrorHandlerUseCase,
    override val retryStorage: RetryStorage,
) : BaseViewModelNew<State, Event, Effect>() {

    override fun createInitialState(): State = State()

    private var allPosts: List<PostDomain> = emptyList()
    private val searchQueryFlow = MutableStateFlow("")
    private val mediaFilterFlow = MutableStateFlow(PostMediaFilter())
    private val groupByAuthorFlow = MutableStateFlow(false)
    private var observeSearchJob: Job? = null

    /** Повторная попытка должна обновлять данные, но не переинициализировать выбранный сайт. */
    override fun onRetry() {
        onEvent(Event.Load(refresh = true))
    }

    /** Подписка на UI-настройки (вид/размер карточек, формат даты и т.д.). */
    private fun observeUiSetting() {
        uiSetting.prefs.distinctUntilChanged()
            .onEach { model ->
                setState { copy(uiSettingModel = model) }
            }
            .launchIn(viewModelScope)
    }

    init {
        observeUiSetting()
        loadSelectedSite()
    }

    override fun onEvent(event: Event) {
        when (event) {
            Event.Back -> navManager.back()
            is Event.SearchQueryChanged -> onSearchQueryChanged(event.query)
            Event.ToggleHasVideo -> toggleHasVideo()
            Event.ToggleHasAttachments -> toggleHasAttachments()
            Event.ToggleHasImages -> toggleHasImages()
            Event.ToggleGroupByAuthor -> toggleGroupByAuthor()
            is Event.Load -> load(event.refresh)
            is Event.NavigateToPost -> navigateToPost(event.post)
            is Event.NavigateToProfile -> navigateToProfile(event.service, event.creatorId)
        }
    }

    /** Обновляет строку поиска в state и flow (реальный запрос уходит с debounce). */
    private fun onSearchQueryChanged(query: String) {
        searchQueryFlow.value = query
        setState { copy(searchQuery = query) }
    }

    @OptIn(FlowPreview::class)
    /** Единый пайплайн фильтров/поиска, который пересоздает paging flow при изменениях. */
    private fun startObserveSearchOnce() {
        if (observeSearchJob != null) return

        observeSearchJob = combine(
            searchQueryFlow
                .debounce(500L)
                .map { it.trim() }
                .distinctUntilChanged(),
            mediaFilterFlow,
            groupByAuthorFlow
        ) { query, mediaFilter, groupByAuthor ->
            Triple(query, mediaFilter, groupByAuthor)
        }
            .distinctUntilChanged()
            .onEach { (query, mediaFilter, groupByAuthor) ->
                val pagingFlow = getFavoritePostsPagingUseCase(
                    site = currentState.selectSite,
                    query = query,
                    groupByAuthor = groupByAuthor,
                )
                setState {
                    copy(
                        groupedPosts = filterGroupedPosts(
                            items = allPosts,
                            query = query,
                            mediaFilter = mediaFilter,
                        ),
                        posts = if (mediaFilter.isActive) {
                            pagingFlow.map { page ->
                                page.filter { post ->
                                    post.matchesMediaFilter(mediaFilter)
                                }
                            }.cachedIn(viewModelScope)
                        } else {
                            pagingFlow.cachedIn(viewModelScope)
                        }
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    private fun filterGroupedPosts(
        items: List<PostDomain>,
        query: String,
        mediaFilter: PostMediaFilter,
    ): List<PostDomain> {
        val normalizedQuery = query.trim()
        val queryEnabled = normalizedQuery.length >= 2

        return items.asSequence()
            .sortedWith(
                compareByDescending<PostDomain> { it.favedSeq != null }
                    .thenByDescending { it.favedSeq ?: Int.MIN_VALUE }
                    .thenByDescending { it.id }
            )
            .filter { post ->
                !queryEnabled || post.title?.contains(normalizedQuery, ignoreCase = true) == true
            }
            .filter { post ->
                !mediaFilter.isActive || post.matchesMediaFilter(mediaFilter)
            }
            .toList()
    }


    /**
     * Инициализация экрана: выбираем сайт из navigation args, иначе берём уже активный в app state.
     * Затем синхронизируем favorites и запускаем observe-пайплайн фильтров/поиска.
     */
    private fun loadSelectedSite() = viewModelScope.launch {
        val selectSite = navigationStorage.consume<SelectedSite>(KEY_SELECT_SITE)
            ?: selectedSiteUseCase.getSite()

        selectedSiteUseCase.setSiteAndAwait(selectSite)

        setState { copy(selectSite = selectSite) }

        load(refresh = false)

        startObserveSearchOnce()

        searchQueryFlow.value = currentState.searchQuery
    }

    /**
     * Загружает отображаемые имена авторов для grouped-режима.
     * Использует легковесные composite-keys из БД без чтения полных PostDomain.
     */
    private suspend fun loadAuthorNames() {
        val compositeKeys = runCatching {
            storeFavoritePostsRepository.getAllAuthorCompositeKeys(currentState.selectSite)
        }.getOrDefault(emptySet())

        if (compositeKeys.isEmpty()) {
            setState { copy(authorNamesByKey = emptyMap()) }
            return
        }

        val namesByKey = runCatching {
            storeCreatorsRepository.getNamesByCompositeKeys(
                site = currentState.selectSite,
                compositeKeys = compositeKeys,
            )
        }.getOrDefault(emptyMap())

        setState { copy(authorNamesByKey = namesByKey) }
    }

    /** Синхронизирует favorite posts с сетью и обновляет ancillary state. */
    private fun load(refresh: Boolean = false) = viewModelScope.launch {
        if (currentState.loading) return@launch

        setState { copy(loading = true) }

        runCatching {
            getFavoritePostsUseCase(site = currentState.selectSite, refresh = refresh)
        }.onSuccess { posts ->
            allPosts = posts.sortedWith(
                compareByDescending<PostDomain> { it.favedSeq != null }
                    .thenByDescending { it.favedSeq ?: Int.MIN_VALUE }
                    .thenByDescending { it.id }
            )
            setState {
                copy(
                    groupedPosts = filterGroupedPosts(
                        items = allPosts,
                        query = searchQueryFlow.value,
                        mediaFilter = mediaFilterFlow.value,
                    )
                )
            }
        }.onFailure { t ->
            errorHandler.parse(t)
        }

        if (currentState.groupByAuthorEnabled || currentState.authorNamesByKey.isEmpty()) {
            loadAuthorNames()
        }

        setState { copy(loading = false) }
    }

    /** Открытие поста */
    private fun navigateToPost(post: PostDomain) {
        viewModelScope.launch {
            navManager.navigate(
                creatorPostNavigator.getCreatorPostDest(
                    id = post.userId,
                    service = post.service,
                    postId = post.id,
                    showBarCreator = true
                )
            )
        }
    }

    private fun navigateToProfile(service: String, creatorId: String) {
        viewModelScope.launch {
            navManager.navigate(
                creatorProfileNavigator.getCreatorProfileDest(
                    service = service,
                    id = creatorId,
                )
            )
        }
    }

    private fun toggleHasVideo() {
        val current = currentState.mediaFilter
        val next = current.copy(hasVideo = !current.hasVideo)
        mediaFilterFlow.value = next
        setState {
            copy(mediaFilter = next)
        }
    }

    private fun toggleHasAttachments() {
        val current = currentState.mediaFilter
        val next = current.copy(hasAttachments = !current.hasAttachments)
        mediaFilterFlow.value = next
        setState {
            copy(mediaFilter = next)
        }
    }

    private fun toggleHasImages() {
        val current = currentState.mediaFilter
        val next = current.copy(hasImages = !current.hasImages)
        mediaFilterFlow.value = next
        setState {
            copy(mediaFilter = next)
        }
    }

    private fun toggleGroupByAuthor() {
        val enableGrouping = !currentState.groupByAuthorEnabled
        setState { copy(groupByAuthorEnabled = enableGrouping) }
        groupByAuthorFlow.value = enableGrouping
        if (enableGrouping && currentState.authorNamesByKey.isEmpty()) {
            viewModelScope.launch { loadAuthorNames() }
        }
    }

}
