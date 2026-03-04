package su.afk.kemonos.posts.presenter.pagePopularPosts

import androidx.paging.cachedIn
import androidx.paging.filter
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.domain.models.PostDomain
import su.afk.kemonos.error.error.IErrorHandlerUseCase
import su.afk.kemonos.error.error.storage.RetryStorage
import su.afk.kemonos.posts.domain.model.popular.Period
import su.afk.kemonos.posts.domain.model.popular.PopularNavSlot
import su.afk.kemonos.posts.domain.pagingPopular.GetPopularPostsUseCase
import su.afk.kemonos.posts.presenter.common.observeBlacklistedAuthorKeys
import su.afk.kemonos.posts.presenter.common.observeDistinct
import su.afk.kemonos.posts.presenter.delegates.NavigateToPostDelegate
import su.afk.kemonos.posts.presenter.pagePopularPosts.PopularPostsState.*
import su.afk.kemonos.posts.presenter.pagePopularPosts.model.PopularRequest
import su.afk.kemonos.posts.presenter.pagePopularPosts.utils.tripleFor
import su.afk.kemonos.preferences.site.ISelectedSiteUseCase
import su.afk.kemonos.preferences.ui.IUiSettingUseCase
import su.afk.kemonos.storage.api.repository.blacklist.IStoreBlacklistedAuthorsRepository
import su.afk.kemonos.storage.api.repository.blacklist.blacklistKey
import su.afk.kemonos.ui.presenter.changeSite.SiteAwareBaseViewModelNew
import javax.inject.Inject

@HiltViewModel
internal class PopularPostsViewModel @Inject constructor(
    private val getPopularPostsUseCase: GetPopularPostsUseCase,
    private val navigateToPostDelegate: NavigateToPostDelegate,
    private val uiSetting: IUiSettingUseCase,
    private val blacklistedAuthorsRepository: IStoreBlacklistedAuthorsRepository,
    override val selectedSiteUseCase: ISelectedSiteUseCase,
    override val errorHandler: IErrorHandlerUseCase,
    override val retryStorage: RetryStorage,
) : SiteAwareBaseViewModelNew<State, Event, Effect>() {
    private val popularRequestFlow = MutableStateFlow<PopularRequest?>(null)
    private var manualRefreshCounter = 0L

    override fun createInitialState(): State = State()

    init {
        observeUiSetting()
        observePopular()
        initSiteAware()
    }

    override fun onRetry() {
        submitCurrentRequest(forceRefresh = true)
    }

    override suspend fun reloadSite(site: SelectedSite) {
        popularRequestFlow.value = PopularRequest(
            site = site,
            date = null,
            period = Period.RECENT,
        )
    }

    override fun onEvent(event: Event) {
        when (event) {
            is Event.LoadPopular -> submitRequest(date = event.date, period = event.period)
            Event.PullRefresh -> onPullRefresh()
            is Event.PeriodSlotClick -> onPeriodSlotClick(event.period, event.slot)
            is Event.NavigateToPost -> navigateToPost(event.post)
            Event.SwitchSite -> switchSite()
        }
    }

    private fun observeUiSetting() {
        uiSetting.observeDistinct(viewModelScope) { model ->
            setState { copy(uiSettingModel = model) }
        }
    }

    private fun observePopular() {
        var lastManualRefreshCounter = 0L
        val blacklistedKeysFlow = blacklistedAuthorsRepository.observeBlacklistedAuthorKeys()

        combine(
            popularRequestFlow.filterNotNull(),
            blacklistedKeysFlow,
        ) { request, blacklistedKeys ->
            request to blacklistedKeys
        }
            .onEach { (request, blacklistedKeys) ->
                val forceRefresh = request.manualRefreshCounter != lastManualRefreshCounter
                lastManualRefreshCounter = request.manualRefreshCounter

                loadPopular(
                    site = request.site,
                    date = request.date,
                    period = request.period,
                    blacklistedKeys = blacklistedKeys,
                    forceRefresh = forceRefresh,
                )
            }
            .launchIn(viewModelScope)
    }

    private fun submitCurrentRequest(forceRefresh: Boolean) {
        val refreshCounter = nextRefreshCounter(forceRefresh)
        val request = popularRequestFlow.value ?: PopularRequest(
            site = site.value,
            date = currentState.popularDateForPopular,
            period = currentState.popularPeriod,
            manualRefreshCounter = refreshCounter,
        )
        popularRequestFlow.value = null
        popularRequestFlow.value = request.copy(manualRefreshCounter = refreshCounter)
    }

    private fun submitRequest(
        date: String?,
        period: Period,
        forceRefresh: Boolean = false,
    ) {
        val refreshCounter = nextRefreshCounter(forceRefresh)
        popularRequestFlow.value = PopularRequest(
            site = site.value,
            date = date,
            period = period,
            manualRefreshCounter = refreshCounter,
        )
    }

    private fun loadPopular(
        site: SelectedSite,
        date: String?,
        period: Period,
        blacklistedKeys: Set<String>,
        forceRefresh: Boolean,
    ) {
        setState { copy(popularDateForPopular = date, popularPeriod = period) }

        val flow = getPopularPostsUseCase(
            site = site,
            date = date,
            period = period,
            onMeta = { popular ->
                setState {
                    copy(
                        popularPostsInfo = popular.info,
                        popularProps = popular.props,
                    )
                }
            },
            forceRefresh = forceRefresh,
        ).let { pagingFlow ->
            if (blacklistedKeys.isEmpty()) {
                pagingFlow.cachedIn(viewModelScope)
            } else {
                pagingFlow
                    .map { page ->
                        page.filter { post ->
                            !blacklistedKeys.contains(
                                blacklistKey(post.service, post.userId),
                            )
                        }
                    }
                    .cachedIn(viewModelScope)
            }
        }

        setState { copy(posts = flow) }
    }

    private fun onPullRefresh() {
        submitCurrentRequest(forceRefresh = true)
    }

    private fun nextRefreshCounter(forceRefresh: Boolean): Long {
        if (forceRefresh) {
            manualRefreshCounter += 1
        }
        return manualRefreshCounter
    }

    private fun onPeriodSlotClick(period: Period, slot: PopularNavSlot) {
        val navDates = currentState.popularPostsInfo?.navigationDates ?: return
        val triple = navDates.tripleFor(period) ?: return

        val date = when (slot) {
            PopularNavSlot.PREV -> triple.first
            PopularNavSlot.CURRENT -> triple.second
            PopularNavSlot.NEXT -> triple.third
        } ?: return

        submitRequest(date = date, period = period)
    }

    private fun navigateToPost(post: PostDomain) {
        navigateToPostDelegate.navigateToPost(post)
    }
}
