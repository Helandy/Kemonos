package su.afk.kemonos.creatorProfile.presenter.creatorProfile.model

import kotlinx.serialization.Serializable
import su.afk.kemonos.preferences.ui.CreatorProfileTabKey
import su.afk.kemonos.ui.R

@Serializable
internal enum class ProfileTab(labelRes: Int) {
    POSTS(R.string.profile_tab_posts),
    ANNOUNCEMENTS(R.string.profile_tab_announcements),
    FANCARD(R.string.profile_tab_fancard),
    TAGS(R.string.tags),
    DMS(R.string.profile_tab_dms),
    LINKS(R.string.profile_tab_links),
    SIMILAR(R.string.profile_tab_similar),
    COMMUNITY(R.string.profile_tab_community);

    @get:androidx.annotation.StringRes
    val labelRes = labelRes

    companion object {
        fun ProfileTab.toCreatorProfileTabKey(): CreatorProfileTabKey = when (this) {
            POSTS -> CreatorProfileTabKey.POSTS
            ANNOUNCEMENTS -> CreatorProfileTabKey.ANNOUNCEMENTS
            FANCARD -> CreatorProfileTabKey.FANCARD
            DMS -> CreatorProfileTabKey.DMS
            TAGS -> CreatorProfileTabKey.TAGS
            LINKS -> CreatorProfileTabKey.LINKS
            SIMILAR -> CreatorProfileTabKey.SIMILAR
            COMMUNITY -> CreatorProfileTabKey.COMMUNITY
        }
    }
}
