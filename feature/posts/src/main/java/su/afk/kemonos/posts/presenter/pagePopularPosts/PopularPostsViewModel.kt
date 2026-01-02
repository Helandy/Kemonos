package su.afk.kemonos.posts.presenter.pagePopularPosts

import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import su.afk.kemonos.common.error.IErrorHandlerUseCase
import su.afk.kemonos.common.error.storage.RetryStorage
import su.afk.kemonos.common.presenter.changeSite.SiteAwareBaseViewModel
import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.domain.models.PostDomain
import su.afk.kemonos.posts.domain.model.popular.Period
import su.afk.kemonos.posts.domain.model.popular.PopularNavSlot
import su.afk.kemonos.posts.domain.pagingPopular.GetPopularPostsUseCase
import su.afk.kemonos.posts.presenter.common.NavigateToPostDelegate
import su.afk.kemonos.posts.presenter.pagePopularPosts.utils.tripleFor
import su.afk.kemonos.preferences.site.ISelectedSiteUseCase
import javax.inject.Inject

@HiltViewModel
internal class PopularPostsViewModel @Inject constructor(
    private val getPopularPostsUseCase: GetPopularPostsUseCase,
    private val navigateToPostDelegate: NavigateToPostDelegate,
    override val selectedSiteUseCase: ISelectedSiteUseCase,
    override val errorHandler: IErrorHandlerUseCase,
    override val retryStorage: RetryStorage,
) : SiteAwareBaseViewModel<PopularPostsState>(
    initialState = PopularPostsState()
) {

    override fun onRetry() {
        viewModelScope.launch {
            val s = state.value
            loadPopular(
                date = s.popularDateForPopular,
                period = s.popularPeriod ?: Period.RECENT
            )
        }
    }

    init {
        initSiteAware()
    }

    override suspend fun reloadSite(site: SelectedSite) {
        loadPopular(date = null, period = Period.RECENT)
    }

    /** Пересоздать paging под текущий site + date/period */
    fun loadPopular(date: String?, period: Period) {
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
    fun onPeriodSlotClick(period: Period, slot: PopularNavSlot) {
        val navDates = state.value.popularPostsInfo?.navigationDates ?: return
        val triple = navDates.tripleFor(period) ?: return

        val date = when (slot) {
            PopularNavSlot.PREV -> triple.first
            PopularNavSlot.CURRENT -> triple.second
            PopularNavSlot.NEXT -> triple.third
        } ?: return

        loadPopular(date = date, period = period)
    }

    fun navigateToPost(post: PostDomain) = navigateToPostDelegate.navigateToPost(post)

    fun parseError(t: Throwable) = errorHandler.parse(t)
}