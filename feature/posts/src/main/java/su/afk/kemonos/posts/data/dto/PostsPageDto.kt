package su.afk.kemonos.posts.data.dto

import com.google.gson.annotations.SerializedName
import su.afk.kemonos.common.data.common.PostUnifiedDto

internal data class PostsDto(
    @SerializedName("count")
    val count: Int? = 0,
    @SerializedName("posts")
    val posts: List<PostUnifiedDto>?,
    @SerializedName("true_count")
    val trueCount: Int? = 0,
)