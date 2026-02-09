package su.afk.kemonos.posts.presenter.pagePopularPosts

import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import su.afk.kemonos.common.error.IErrorHandlerUseCase
import su.afk.kemonos.common.error.storage.RetryStorage
import su.afk.kemonos.common.presenter.changeSite.SiteAwareBaseViewModelNew
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.domain.models.PostDomain
import su.afk.kemonos.posts.domain.model.popular.Period
import su.afk.kemonos.posts.domain.model.popular.PopularNavSlot
import su.afk.kemonos.posts.domain.pagingPopular.GetPopularPostsUseCase
import su.afk.kemonos.posts.presenter.common.NavigateToPostDelegate
import su.afk.kemonos.posts.presenter.pagePopularPosts.PopularPostsState.*
import su.afk.kemonos.posts.presenter.pagePopularPosts.utils.tripleFor
import su.afk.kemonos.preferences.site.ISelectedSiteUseCase
import su.afk.kemonos.preferences.ui.IUiSettingUseCase
import javax.inject.Inject

@HiltViewModel
internal class PopularPostsViewModel @Inject constructor(
    private val getPopularPostsUseCase: GetPopularPostsUseCase,
    private val navigateToPostDelegate: NavigateToPostDelegate,
    private val uiSetting: IUiSettingUseCase,
    override val selectedSiteUseCase: ISelectedSiteUseCase,
    override val errorHandler: IErrorHandlerUseCase,
    override val retryStorage: RetryStorage,
) : SiteAwareBaseViewModelNew<State, Event, Effect>() {

    override fun createInitialState(): State = State()

    override fun onRetry() {
        viewModelScope.launch {
            val s = currentState
            loadPopular(
                date = s.popularDateForPopular,
                period = s.popularPeriod
            )
        }
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
        initSiteAware()
    }

    override suspend fun reloadSite(site: SelectedSite) {
        loadPopular(date = null, period = Period.RECENT)
    }

    override fun onEvent(event: Event) {
        when (event) {
            is Event.LoadPopular -> loadPopular(date = event.date, period = event.period)
            is Event.PeriodSlotClick -> onPeriodSlotClick(event.period, event.slot)
            is Event.NavigateToPost -> navigateToPost(event.post)
            Event.SwitchSite -> switchSite()
        }
    }

    /** Пересоздать paging под текущий site + date/period */
    private fun loadPopular(date: String?, period: Period) {
        setState { copy(popularDateForPopular = date, popularPeriod = period) }

        val currentSite = site.value

        val flow = getPopularPostsUseCase(
            site = currentSite,
            date = date,
            period = period,
            onMeta = { popular ->
                setState {
                    copy(
                        popularPostsInfo = popular.info,
                        popularProps = popular.props,
                    )
                }
            }
        ).cachedIn(viewModelScope)

        setState { copy(posts = flow) }
    }

    /** Клик по стрелке / центру в панели периодов */
    private fun onPeriodSlotClick(period: Period, slot: PopularNavSlot) {
        val navDates = currentState.popularPostsInfo?.navigationDates ?: return
        val triple = navDates.tripleFor(period) ?: return

        val date = when (slot) {
            PopularNavSlot.PREV -> triple.first
            PopularNavSlot.CURRENT -> triple.second
            PopularNavSlot.NEXT -> triple.third
        } ?: return

        loadPopular(date = date, period = period)
    }

    private fun navigateToPost(post: PostDomain) {
        navigateToPostDelegate.navigateToPost(post)
    }
}
