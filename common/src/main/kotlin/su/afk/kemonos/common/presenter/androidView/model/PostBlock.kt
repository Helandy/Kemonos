package su.afk.kemonos.common.presenter.androidView.model

sealed interface PostBlock {
    data class Html(val html: String) : PostBlock
    data class Image(val url: String) : PostBlock
    data class Video(val url: String, val poster: String?) : PostBlock
    data class Audio(val url: String) : PostBlock
}