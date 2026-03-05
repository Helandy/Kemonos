package su.afk.kemonos.posts.presenter.tagsSelect

import androidx.paging.cachedIn
import androidx.paging.filter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import su.afk.kemonos.domain.models.PostDomain
import su.afk.kemonos.error.error.IErrorHandlerUseCase
import su.afk.kemonos.error.error.storage.RetryStorage
import su.afk.kemonos.navigation.NavigationManager
import su.afk.kemonos.navigation.storage.NavigationStorage
import su.afk.kemonos.posts.domain.pagingSearch.GetSearchPostsPagingUseCase
import su.afk.kemonos.posts.presenter.common.observeBlacklistedAuthorKeys
import su.afk.kemonos.posts.presenter.common.observeDistinct
import su.afk.kemonos.posts.presenter.delegates.NavigateToPostDelegate
import su.afk.kemonos.posts.presenter.tagsSelect.TagsSelectState.*
import su.afk.kemonos.posts.util.Const.TAGS_SELECTED_NAV_KEY
import su.afk.kemonos.preferences.site.ISelectedSiteUseCase
import su.afk.kemonos.preferences.ui.IUiSettingUseCase
import su.afk.kemonos.storage.api.repository.blacklist.IStoreBlacklistedAuthorsRepository
import su.afk.kemonos.storage.api.repository.blacklist.blacklistKey
import su.afk.kemonos.ui.components.posts.filter.PostMediaFilter
import su.afk.kemonos.ui.components.posts.filter.matchesMediaFilter
import su.afk.kemonos.ui.presenter.baseViewModel.BaseViewModelNew
import javax.inject.Inject

@HiltViewModel
internal class TagsSelectViewModel @Inject constructor(
    private val selectedSiteUseCase: ISelectedSiteUseCase,
    private val getSearchPostsPagingUseCase: GetSearchPostsPagingUseCase,
    private val navigateToPostDelegate: NavigateToPostDelegate,
    private val navManager: NavigationManager,
    private val navigationStorage: NavigationStorage,
    private val uiSetting: IUiSettingUseCase,
    private val blacklistedAuthorsRepository: IStoreBlacklistedAuthorsRepository,
    override val errorHandler: IErrorHandlerUseCase,
    override val retryStorage: RetryStorage,
) : BaseViewModelNew<State, Event, Effect>() {

    private val selectedTagFlow = MutableStateFlow<String?>(null)
    private val mediaFilterFlow = MutableStateFlow(PostMediaFilter())
    private val blacklistedAuthorKeysFlow = MutableStateFlow<Set<String>>(emptySet())
    private val manualRefreshCounterFlow = MutableStateFlow(0L)

    override fun createInitialState(): State = State()

    init {
        observeUiSetting()
        observeTagContentAndBlacklist()

        val selectedTag = navigationStorage.consume<String>(TAGS_SELECTED_NAV_KEY)
            ?.trim()
            ?.ifEmpty { null }
        selectedTagFlow.value = selectedTag
    }

    override fun onRetry() {
        triggerManualRefresh()
    }

    private fun observeUiSetting() {
        uiSetting.observeDistinct(viewModelScope) { model ->
            setState { copy(uiSettingModel = model) }
        }
    }

    override fun onEvent(event: Event) {
        when (event) {
            Event.Back -> navManager.back()
            Event.PullRefresh -> onPullRefresh()
            is Event.NavigateToPost -> navigateToPost(event.post)
            Event.ToggleHasVideo -> toggleHasVideo()
            Event.ToggleHasAttachments -> toggleHasAttachments()
            Event.ToggleHasImages -> toggleHasImages()
        }
    }

    private fun observeTagContentAndBlacklist() {
        var lastManualRefreshCounter = 0L

        combine(
            selectedTagFlow,
            mediaFilterFlow,
            blacklistedAuthorsRepository.observeBlacklistedAuthorKeys(),
            manualRefreshCounterFlow,
        ) { tag, mediaFilter, blacklistedAuthorKeys, manualRefreshCounter ->
            TagLoadRequest(
                tag = tag,
                mediaFilter = mediaFilter,
                blacklistedAuthorKeys = blacklistedAuthorKeys,
                manualRefreshCounter = manualRefreshCounter,
            )
        }.distinctUntilChanged()
            .onEach { request ->
                val forceRefresh = request.manualRefreshCounter != lastManualRefreshCounter
                lastManualRefreshCounter = request.manualRefreshCounter
                blacklistedAuthorKeysFlow.value = request.blacklistedAuthorKeys
                requestPage(
                    tag = request.tag,
                    mediaFilter = request.mediaFilter,
                    blacklistedAuthorKeys = request.blacklistedAuthorKeys,
                    forceRefresh = forceRefresh,
                )
            }
            .launchIn(viewModelScope)
    }

    private fun reloadCurrentTag() {
        val tag = currentState.selectedTag
        if (tag == null) {
            setState { copy(selectedTag = null, posts = emptyFlow()) }
            return
        }

        requestPage(
            tag = tag,
            mediaFilter = currentState.mediaFilter,
            blacklistedAuthorKeys = blacklistedAuthorKeysFlow.value,
            forceRefresh = false,
        )
    }

    private fun requestPage(
        tag: String?,
        mediaFilter: PostMediaFilter,
        blacklistedAuthorKeys: Set<String>,
        forceRefresh: Boolean,
    ) {
        if (tag == null) {
            setState {
                copy(
                    selectedTag = null,
                    mediaFilter = mediaFilter,
                    posts = emptyFlow(),
                )
            }
            return
        }

        val currentSite = selectedSiteUseCase.getSite()
        val pagingFlow = getSearchPostsPagingUseCase(
            site = currentSite,
            tag = tag,
            search = null,
            forceRefresh = forceRefresh,
        )

        setState {
            copy(
                selectedTag = tag,
                mediaFilter = mediaFilter,
                posts = if (mediaFilter.isActive || blacklistedAuthorKeys.isNotEmpty()) {
                    pagingFlow.map { page ->
                        page.filter { post ->
                            val allowedByBlacklist = !blacklistedAuthorKeys.contains(
                                blacklistKey(post.service, post.userId)
                            )
                            allowedByBlacklist && post.matchesMediaFilter(mediaFilter)
                        }
                    }.cachedIn(viewModelScope)
                } else {
                    pagingFlow.cachedIn(viewModelScope)
                },
            )
        }
    }

    private fun toggleHasVideo() {
        val current = currentState.mediaFilter
        val next = current.copy(hasVideo = !current.hasVideo)
        mediaFilterFlow.value = next
        setState { copy(mediaFilter = next) }
    }

    private fun toggleHasAttachments() {
        val current = currentState.mediaFilter
        val next = current.copy(hasAttachments = !current.hasAttachments)
        mediaFilterFlow.value = next
        setState { copy(mediaFilter = next) }
    }

    private fun toggleHasImages() {
        val current = currentState.mediaFilter
        val next = current.copy(hasImages = !current.hasImages)
        mediaFilterFlow.value = next
        setState { copy(mediaFilter = next) }
    }

    private fun navigateToPost(post: PostDomain) {
        viewModelScope.launch {
            navigateToPostDelegate.navigateToPost(post = post)
        }
    }

    private fun onPullRefresh() {
        triggerManualRefresh()
    }

    private fun triggerManualRefresh() {
        if (currentState.selectedTag == null) {
            reloadCurrentTag()
            return
        }
        manualRefreshCounterFlow.update { it + 1 }
    }

    private data class TagLoadRequest(
        val tag: String?,
        val mediaFilter: PostMediaFilter,
        val blacklistedAuthorKeys: Set<String>,
        val manualRefreshCounter: Long,
    )
}
