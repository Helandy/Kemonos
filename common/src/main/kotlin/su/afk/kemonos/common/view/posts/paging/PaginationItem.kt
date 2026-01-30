package su.afk.kemonos.common.view.posts.paging

sealed interface PaginationItem {
    data class Page(val index: Int) : PaginationItem
    object Ellipsis : PaginationItem
}