package su.afk.kemonos.creatorProfile.presenter.creatorProfile.model

import androidx.annotation.StringRes
import su.afk.kemonos.ui.R

internal enum class ProfileTab(@StringRes val labelRes: Int) {
    POSTS(R.string.profile_tab_posts),
    ANNOUNCEMENTS(R.string.profile_tab_announcements),
    FANCARD(R.string.profile_tab_fancard),
    TAGS(R.string.tags),
    DMS(R.string.profile_tab_dms),
    LINKS(R.string.profile_tab_links),
    SIMILAR(R.string.profile_tab_similar),
    COMMUNITY(R.string.profile_tab_community);
}
