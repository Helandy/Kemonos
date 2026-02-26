package su.afk.kemonos.creatorProfile.presenter.creatorProfile

import androidx.paging.cachedIn
import androidx.paging.filter
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import su.afk.kemonos.creatorProfile.api.IGetProfileUseCase
import su.afk.kemonos.creatorProfile.api.domain.models.profileCommunity.CommunityChannel
import su.afk.kemonos.creatorProfile.domain.paging.GetProfilePostsPagingUseCase
import su.afk.kemonos.creatorProfile.navigation.CreatorDest
import su.afk.kemonos.creatorProfile.presenter.creatorProfile.delegates.LikeDelegate
import su.afk.kemonos.creatorProfile.presenter.creatorProfile.delegates.LoadingTabsContent
import su.afk.kemonos.creatorProfile.presenter.creatorProfile.delegates.NavigationDelegate
import su.afk.kemonos.creatorProfile.presenter.creatorProfile.model.ProfileTab
import su.afk.kemonos.creatorProfile.util.Utils.queryKey
import su.afk.kemonos.domain.models.Tag
import su.afk.kemonos.error.error.IErrorHandlerUseCase
import su.afk.kemonos.error.error.storage.RetryStorage
import su.afk.kemonos.error.error.toFavoriteToastBar
import su.afk.kemonos.navigation.NavigationManager
import su.afk.kemonos.preferences.GetKemonoRootUrlUseCase
import su.afk.kemonos.preferences.IGetCurrentSiteRootUrlUseCase
import su.afk.kemonos.preferences.ui.IUiSettingUseCase
import su.afk.kemonos.storage.api.repository.blacklist.BlacklistedAuthor
import su.afk.kemonos.storage.api.repository.blacklist.IStoreBlacklistedAuthorsRepository
import su.afk.kemonos.storage.api.repository.profilePosts.IStorageCreatorPostsRepository
import su.afk.kemonos.ui.components.posts.filter.matchesMediaFilter
import su.afk.kemonos.ui.presenter.baseViewModel.BaseViewModelNew
import su.afk.kemonos.ui.shared.ShareLinkBuilder
import su.afk.kemonos.ui.shared.model.ShareTarget

internal class CreatorProfileViewModel @AssistedInject constructor(
    @Assisted private val dest: CreatorDest.CreatorProfile,
    private val getProfileUseCase: IGetProfileUseCase,
    private val getKemonoRootUrlUseCase: GetKemonoRootUrlUseCase,
    private val getCurrentSiteRootUrlUseCase: IGetCurrentSiteRootUrlUseCase,
    private val likeDelegate: LikeDelegate,
    private val navigationDelegate: NavigationDelegate,
    private val loadingTabsContent: LoadingTabsContent,
    private val getProfilePostsPagingUseCase: GetProfilePostsPagingUseCase,
    private val navManager: NavigationManager,
    private val postsCache: IStorageCreatorPostsRepository,
    private val blacklistedAuthorsRepository: IStoreBlacklistedAuthorsRepository,
    private val uiSetting: IUiSettingUseCase,
    override val errorHandler: IErrorHandlerUseCase,
    override val retryStorage: RetryStorage,
) : BaseViewModelNew<CreatorProfileState.State, CreatorProfileState.Event, CreatorProfileState.Effect>() {

    private var searchJob: Job? = null
    private var observeBlacklistJob: Job? = null

    override fun createInitialState(): CreatorProfileState.State = CreatorProfileState.State()

    override fun onEvent(event: CreatorProfileState.Event) {
        when (event) {
            CreatorProfileState.Event.Retry -> onRetry()
            CreatorProfileState.Event.PullRefresh -> onPullRefresh()

            CreatorProfileState.Event.Back -> navManager.back()
            CreatorProfileState.Event.CopyProfileLink -> copyProfileLink()
            is CreatorProfileState.Event.OpenCreatorPlatformLink -> setEffect(CreatorProfileState.Effect.OpenUrl(event.url))

            is CreatorProfileState.Event.OpenImage -> navigationDelegate.navigateToOpenImage(event.url)
            is CreatorProfileState.Event.OpenLinkProfile -> navigationDelegate.navigateToLinkProfile(event.link)
            is CreatorProfileState.Event.OpenPost -> navigationDelegate.navigateToPost(event.post)

            is CreatorProfileState.Event.TabChanged -> setState { copy(selectedTab = event.tab) }
            is CreatorProfileState.Event.OpenCommunityChannel -> openCommunityChannel(event.channel)
            is CreatorProfileState.Event.TagClicked -> clickTag(event.tag)
            CreatorProfileState.Event.ClearTag -> clearTag()

            CreatorProfileState.Event.ToggleSearch -> setState { copy(isSearchVisible = !currentState.isSearchVisible) }
            CreatorProfileState.Event.CloseSearch -> closeSearch()
            is CreatorProfileState.Event.SearchTextChanged -> setSearchText(event.text)

            CreatorProfileState.Event.ToggleHasVideo -> toggleHasVideo()
            CreatorProfileState.Event.ToggleHasAttachments -> toggleHasAttachments()
            CreatorProfileState.Event.ToggleHasImages -> toggleHasImages()
            CreatorProfileState.Event.ToggleBlacklist -> toggleBlacklist()

            CreatorProfileState.Event.FavoriteClick -> onFavoriteClick()
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(dest: CreatorDest.CreatorProfile): CreatorProfileViewModel
    }

    override fun onRetry() {
        loadAll()
    }

    /** UI настройки */
    private fun observeUiSetting() {
        uiSetting.prefs.distinctUntilChanged()
            .onEach { model ->
                setState { copy(uiSettingModel = model) }
            }
            .launchIn(viewModelScope)
    }

    init {
        observeUiSetting()

        setState {
            copy(
                service = dest.service,
                id = dest.id,
                currentTag = dest.tag
            )
        }

        if (!isDiscordProfile(dest.service)) {
            observeBlacklist()
            loadAll()
        }
    }

    private fun loadAll() {
        viewModelScope.launch {
            setState { copy(loading = true) }
            getProfileInfo()
            setTabsToProfile()
            isCreatorFavorite()
            loadProfileAndPosts()
            setState { copy(loading = false) }
        }
    }

    /** Если профиль из дискорда — открываем браузер */
    private fun isDiscordProfile(service: String): Boolean {
        if (service != "discord") return false

        val baseUrl = getKemonoRootUrlUseCase()
        val url = "$baseUrl/$service/server/${currentState.id}"

        if (currentState.discordUrlOpened) return true

        setState { copy(isDiscordProfile = true, loading = false, discordUrlOpened = true) }
        setEffect(CreatorProfileState.Effect.OpenUrl(url))

        return true
    }

    /** Получение информации о Профиле */
    fun getProfileInfo() = viewModelScope.launch {
        val profile = getProfileUseCase(currentState.service, currentState.id)

        setState {
            copy(
                profile = profile,
                countDm = profile?.dmCount,
                countPost = profile?.postCount,
            )
        }
    }

    /** Загрузка постов на странице */
    fun loadProfileAndPosts() = viewModelScope.launch {
        val pagingFlow = getProfilePostsPagingUseCase(
            service = currentState.service,
            id = currentState.id,
            tag = currentState.currentTag?.tag,
            search = currentState.searchText
        )
        val mediaFilter = currentState.mediaFilter

        setState {
            copy(
                profilePosts = if (mediaFilter.isActive) {
                    pagingFlow.map { page ->
                        page.filter { post -> post.matchesMediaFilter(mediaFilter) }
                    }.cachedIn(viewModelScope)
                } else {
                    pagingFlow.cachedIn(viewModelScope)
                },
                loading = false,
            )
        }
    }

    /** Какие вкладки отображать */
    fun setTabsToProfile() = viewModelScope.launch {
        /** базовый набор табов */
        val tabs = mutableListOf(ProfileTab.POSTS)
        /** Лс */
        currentState.countDm?.let { if (it > 0) tabs.add(ProfileTab.DMS) }

        setState { copy(showTabs = tabs) }

        val service = currentState.service
        val id = currentState.id

        val checks = listOf(
            async {
                loadingTabsContent.checkDms(
                    countDm = currentState.countDm,
                    setState = ::setState,
                    service = service,
                    id = id
                )
            },
            async {
                loadingTabsContent.checkFanCard(
                    setState = ::setState,
                    service = service,
                    id = id
                )
            },
            async {
                loadingTabsContent.checkAnnouncements(
                    setState = ::setState,
                    service = service,
                    id = id
                )
            },
            async {
                loadingTabsContent.checkTags(
                    setState = ::setState,
                    service = service,
                    id = id
                )
            },
            async {
                loadingTabsContent.checkLinks(
                    setState = ::setState,
                    service = service,
                    id = id
                )
            },
            async {
                loadingTabsContent.checkSimilar(
                    setState = ::setState,
                    service = service,
                    id = id
                )
            },
            async {
                loadingTabsContent.checkCommunity(
                    setState = ::setState,
                    service = service,
                    id = id
                )
            }
        )
        checks.awaitAll()
    }

    private fun openCommunityChannel(channel: CommunityChannel) {
        navigationDelegate.navigateToCommunityChat(
            service = currentState.service,
            creatorId = currentState.id,
            channel = channel
        )
    }

    /** search on tag */
    fun clickTag(tag: Tag) = viewModelScope.launch {
        setState {
            copy(
                selectedTab = ProfileTab.POSTS,
                currentTag = tag,
                profilePosts = getProfilePostsPagingUseCase(
                    service = currentState.service,
                    id = currentState.id,
                    tag = tag.tag,
                    search = null
                ).cachedIn(viewModelScope),
            )
        }
    }

    /** Сброс выбранного тега с запросом постов и страниц */
    fun clearTag() {
        setState {
            copy(
                selectedTab = ProfileTab.POSTS,
                currentTag = null,
                profilePosts = getProfilePostsPagingUseCase(
                    service = currentState.service,
                    id = currentState.id,
                    tag = null,
                    search = null
                ).cachedIn(viewModelScope),
            )
        }
    }

    /** Поиск по тексту */
    fun setSearchText(text: String) {
        if (text == currentState.searchText) return
        setState { copy(searchText = text) }

        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(2_000)
            loadProfileAndPosts()
        }
    }

    /** скрыть поиск */
    fun closeSearch() {
        searchJob?.cancel()
        setState {
            copy(
                isSearchVisible = false,
                searchText = "",
                mediaFilter = mediaFilter.copy(
                    hasVideo = false,
                    hasAttachments = false,
                    hasImages = false,
                )
            )
        }

        loadProfileAndPosts()
    }

    private fun toggleHasVideo() {
        setState {
            copy(mediaFilter = mediaFilter.copy(hasVideo = !mediaFilter.hasVideo))
        }
        loadProfileAndPosts()
    }

    private fun toggleHasAttachments() {
        setState {
            copy(mediaFilter = mediaFilter.copy(hasAttachments = !mediaFilter.hasAttachments))
        }
        loadProfileAndPosts()
    }

    private fun toggleHasImages() {
        setState {
            copy(mediaFilter = mediaFilter.copy(hasImages = !mediaFilter.hasImages))
        }
        loadProfileAndPosts()
    }

    /** избранное */
    fun onFavoriteClick() = viewModelScope.launch {
        if (currentState.favoriteActionLoading) return@launch

        val wasFavorite = currentState.isFavorite
        setState { copy(favoriteActionLoading = true) }

        val result = likeDelegate.onFavoriteClick(
            isFavorite = currentState.isFavorite,
            service = currentState.service,
            id = currentState.id
        )
        result
            .onSuccess {
                setState { copy(isFavorite = !wasFavorite) }
            }
            .onFailure { t ->
                val errorMessage = errorHandler.parse(t).toFavoriteToastBar()
                setEffect(CreatorProfileState.Effect.ShowToast(errorMessage))
            }
        setState { copy(favoriteActionLoading = false) }
    }

    /** проверит в избранном ли автор */
    fun isCreatorFavorite() = viewModelScope.launch {
        val isShowAvailable = likeDelegate.creatorIsAvailableLike()
        if (isShowAvailable) {
            val favorite = likeDelegate.isCreatorFavorite(
                service = currentState.service,
                id = currentState.id
            )
            setState { copy(isFavorite = favorite) }
        }
        setState { copy(isFavoriteShowButton = isShowAvailable) }
    }

    fun onPullRefresh() = viewModelScope.launch {
        val qk = queryKey(
            service = currentState.service,
            id = currentState.id,
            search = currentState.searchText,
            tag = currentState.currentTag?.tag,
        )
        postsCache.clearQuery(qk)

        loadAll()
    }

    /** Копирование в буфер */
    fun copyProfileLink() {
        val url = ShareLinkBuilder.build(
            ShareTarget.Profile(
                siteRoot = getCurrentSiteRootUrlUseCase(),
                service = currentState.service,
                userId = currentState.id
            )
        )
        setEffect(CreatorProfileState.Effect.CopyPostLink(url))
    }

    private fun observeBlacklist() {
        observeBlacklistJob?.cancel()
        observeBlacklistJob = blacklistedAuthorsRepository.observeContains(
            service = currentState.service,
            creatorId = currentState.id
        )
            .onEach { inBlacklist ->
                setState { copy(isInBlacklist = inBlacklist) }
            }
            .launchIn(viewModelScope)
    }

    private fun toggleBlacklist() = viewModelScope.launch {
        val profile = currentState.profile ?: return@launch

        if (currentState.isInBlacklist) {
            blacklistedAuthorsRepository.remove(
                service = profile.service,
                creatorId = profile.id
            )
            setEffect(CreatorProfileState.Effect.RemovedFromBlacklist)
            return@launch
        }

        blacklistedAuthorsRepository.upsert(
            BlacklistedAuthor(
                service = profile.service,
                creatorId = profile.id,
                creatorName = profile.name,
                createdAt = System.currentTimeMillis()
            )
        )
        setEffect(CreatorProfileState.Effect.AddedToBlacklist)
    }
}
