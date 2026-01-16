package su.afk.kemonos.preferences.ui

enum class CreatorViewMode { LIST, GRID }
//enum class PostsViewMode { GRID, LIST }
//enum class PostPageMode { NORMAL, ONE_POST_PER_PAGE }

//enum class DownloadPathTemplate {
//    USER_POST_DATE_ID, // User/Post.Date.Id
//    POST_DATE_ID_USER, // Post.Date.Id.User
//    GROUP_BY_USER,     // User/...
//}

//enum class PopularDateFormat { MONTH_MONTH, DD_MM } // пример, настроишь как надо
//enum class TranslationTarget { APP, GOOGLE_TRANSLATE } // LATER "открывать гугл переводчик"

data class UiSettingModel(
    /** Вид отображения авторов  */
    val creatorsViewMode: CreatorViewMode = DEFAULT_CREATORS_VIEW_MODE,

    /** debug-only: пропустить проверку API при входе */
    val skipApiCheckOnLogin: Boolean = false,

//    val postsViewMode: PostsViewMode = PostsViewMode.GRID,
//    val postPageMode: PostPageMode = PostPageMode.NORMAL, // LATER

//    val downloadFolderUri: String? = null,               // LATER (SAF uri)
//    val downloadCatalogVersion: Int = 1,                 // LATER
//    val downloadPathTemplate: DownloadPathTemplate = DownloadPathTemplate.USER_POST_DATE_ID, // LATER

//    val suggestRandomCreatorsOnLaunch: Boolean = false,  // LATER
//
//    val popularDateFormat: PopularDateFormat = PopularDateFormat.MONTH_MONTH, // LATER
//
//    val translateTarget: TranslationTarget = TranslationTarget.APP, // LATER
//    val translateLanguage: String = "en",                              // LATER конечный язык (ISO code)
//
//    val experimentalCalendar: Boolean = false,            // LATER
) {
    companion object {
        val DEFAULT_CREATORS_VIEW_MODE = CreatorViewMode.LIST
    }
}