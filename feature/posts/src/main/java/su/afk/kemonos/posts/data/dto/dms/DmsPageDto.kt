package su.afk.kemonos.posts.data.dto.dms

import com.google.gson.annotations.SerializedName
import su.afk.kemonos.posts.domain.model.dms.DmDomain
import su.afk.kemonos.posts.domain.model.dms.DmsPageDomain

internal data class DmsPageDto(
    @SerializedName("props")
    val props: PropsDto? = null,
) {
    internal data class PropsDto(
        @SerializedName("count")
        val count: Int? = null,
        @SerializedName("limit")
        val limit: Int? = null,
        @SerializedName("dms")
        val dms: List<DmDto>? = null,
    )

    internal data class DmDto(
        @SerializedName("added")
        val added: String? = null,
        @SerializedName("artist")
        val artist: ArtistDto? = null,
        @SerializedName("content")
        val content: String? = null,
        @SerializedName("hash")
        val hash: String? = null,
        @SerializedName("published")
        val published: String? = null,
        @SerializedName("service")
        val service: String? = null,
        @SerializedName("user")
        val user: String? = null,
    )

    internal data class ArtistDto(
        @SerializedName("id")
        val id: String? = null,
        @SerializedName("name")
        val name: String? = null,
        @SerializedName("updated")
        val updated: String? = null,
    )
}

internal fun DmsPageDto.toDomain(requestedLimit: Int): DmsPageDomain {
    val props = props
    val dms = props?.dms.orEmpty().map { dto ->
        val artistId = dto.artist?.id ?: dto.user.orEmpty()
        val artistName = dto.artist?.name ?: artistId
        DmDomain(
            added = dto.added.orEmpty(),
            content = dto.content.orEmpty(),
            hash = dto.hash.orEmpty(),
            published = dto.published.orEmpty(),
            service = dto.service.orEmpty(),
            user = dto.user.orEmpty(),
            artistId = artistId,
            artistName = artistName,
            artistUpdated = dto.artist?.updated,
        )
    }

    return DmsPageDomain(
        count = props?.count ?: dms.size,
        limit = props?.limit ?: requestedLimit,
        dms = dms,
    )
}
