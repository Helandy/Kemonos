package su.afk.kemonos.common.presenter.screens.postsScreen.paging

sealed interface PaginationItem {
    data class Page(val index: Int) : PaginationItem
    object Ellipsis : PaginationItem
}