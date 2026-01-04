package su.afk.kemonos.creatorProfile.data.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import su.afk.kemonos.common.data.dto.PostUnifiedDto
import su.afk.kemonos.creatorProfile.data.dto.profile.ProfileDto
import su.afk.kemonos.creatorProfile.data.dto.profileAnnouncements.ProfileAnnouncementsDto
import su.afk.kemonos.creatorProfile.data.dto.profileDms.DmDto
import su.afk.kemonos.creatorProfile.data.dto.profileFanCards.ProfileFanCardsDto
import su.afk.kemonos.creatorProfile.data.dto.profileLinks.ProfileLinksDto
import su.afk.kemonos.creatorProfile.data.dto.profileTags.TagDto
import su.afk.kemonos.network.textInterceptor.HeaderText

internal interface CreatorProfileApi {

    /** searh and posts and tag */
    @HeaderText
    @GET("v1/{service}/user/{id}/posts")
    suspend fun getProfilePosts(
        @Path("service") service: String,
        @Path("id") id: String,
        @Query("o") offset: Int? = null,
        @Query("q") search: String? = null,
        @Query("tag") tag: String? = null
    ): Response<List<PostUnifiedDto>>

    /** DMs */
    @HeaderText
    @GET("v1/{service}/user/{id}/dms")
    suspend fun getProfileDms(
        @Path("service") service: String,
        @Path("id") id: String,
    ): Response<List<DmDto>>

    /** tags */
    @HeaderText
    @GET("v1/{service}/user/{id}/tags")
    suspend fun getProfileTags(
        @Path("service") service: String,
        @Path("id") id: String,
    ): Response<List<TagDto>>

    /** Announcements  */
    @HeaderText
    @GET("v1/{service}/user/{id}/announcements")
    suspend fun getProfileAnnouncements(
        @Path("service") service: String,
        @Path("id") id: String,
    ): Response<List<ProfileAnnouncementsDto>>

    /** FanCards */
    @HeaderText
    @GET("v1/{service}/user/{id}/fancards")
    suspend fun getProfileFanCards(
        @Path("service") service: String,
        @Path("id") id: String,
    ): Response<List<ProfileFanCardsDto>>

    /** Linked Accounts */
    @HeaderText
    @GET("v1/{service}/user/{id}/links")
    suspend fun getProfileLinks(
        @Path("service") service: String,
        @Path("id") id: String,
    ): Response<List<ProfileLinksDto>>

    /** Профиль */
    @HeaderText
    @GET("v1/{service}/user/{id}/profile")
    suspend fun getProfile(
        @Path("service") service: String,
        @Path("id") id: String
    ): Response<ProfileDto>
}