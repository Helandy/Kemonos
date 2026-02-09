package su.afk.kemonos.app.update.data.dto

import com.google.gson.annotations.SerializedName

internal data class GitHubReleaseDto(
    @SerializedName("tag_name")
    val tagName: String,

    @SerializedName("html_url")
    val htmlUrl: String,

    @SerializedName("body")
    val body: String?,
)