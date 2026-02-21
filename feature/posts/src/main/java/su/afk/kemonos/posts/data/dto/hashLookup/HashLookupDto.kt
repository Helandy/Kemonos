package su.afk.kemonos.posts.data.dto.hashLookup

import com.google.gson.annotations.SerializedName
import su.afk.kemonos.data.dto.PostUnifiedDto
import su.afk.kemonos.data.dto.PostUnifiedDto.Companion.toDomain
import su.afk.kemonos.posts.domain.model.hashLookup.HashLookupDomain

internal data class HashLookupDto(
    @SerializedName("id")
    val id: Long,
    @SerializedName("hash")
    val hash: String,
    @SerializedName("mime")
    val mime: String? = null,
    @SerializedName("ext")
    val ext: String? = null,
    @SerializedName("size")
    val size: Long? = null,
    @SerializedName("posts")
    val posts: List<PostUnifiedDto>? = null,
)

internal fun HashLookupDto.toDomain(): HashLookupDomain = HashLookupDomain(
    id = this.id,
    hash = this.hash,
    mime = this.mime,
    ext = this.ext,
    size = this.size,
    posts = this.posts.orEmpty().map { it.toDomain() },
)
