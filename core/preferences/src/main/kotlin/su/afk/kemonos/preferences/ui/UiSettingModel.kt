package su.afk.kemonos.preferences.ui

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

enum class CreatorViewMode { LIST, GRID }
enum class PostsViewMode { LIST, GRID }

/** Куда переводить */
enum class TranslateTarget {
    APP,        // встроенный перевод
    GOOGLE      // открыть Google Translate
}

/** Где показывать кнопку рандома */
enum class RandomButtonPlacement {
    SCREEN,     // отдельная кнопка на экране
    SEARCH_BAR  // иконка в строке поиска
}

enum class DateFormatMode(val pattern: String) {
    /** 20.01.2026 */
    DD_MM_YYYY("dd.MM.yyyy"),

    /** 20.01.26 */
    DD_MM_YY("dd.MM.yy"),

    /** 20/01/2026 */
    DD_MM_YYYY_SLASH("dd/MM/yyyy"),

    /** 20/01/26 */
    DD_MM_YY_SLASH("dd/MM/yy"),

    /** Jan 20, 2026 */
    MMM_D_YYYY("MMM d, yyyy"),

    /** Jan 20, 26 */
    MMM_D_YY("MMM d, yy"),

    /** 20 Jan 2026 */
    D_MMM_YYYY("d MMM yyyy"),

    /** 20 Jan 26 */
    D_MMM_YY("d MMM yy");

    fun example(locale: Locale = Locale.getDefault()): String =
        LocalDate.of(2026, 1, 20)
            .format(DateTimeFormatter.ofPattern(pattern, locale))
}

data class UiSettingModel(

    /** debug-only: пропустить проверку API при входе */
    val skipApiCheckOnLogin: Boolean = false,

    /** Вид отображения авторов на главной */
    val creatorsViewMode: CreatorViewMode = DEFAULT_CREATORS_VIEW_MODE,

    /** Вид отображения избранное  */
    val creatorsFavoriteViewMode: CreatorViewMode = DEFAULT_CREATORS_VIEW_MODE,

    /** Посты: профиль автора */
    val profilePostsViewMode: PostsViewMode = DEFAULT_POSTS_VIEW_MODE,
    /** Посты: избранное */
    val favoritePostsViewMode: PostsViewMode = DEFAULT_POSTS_VIEW_MODE,
    /** Посты: популярное */
    val popularPostsViewMode: PostsViewMode = DEFAULT_POSTS_VIEW_MODE,
    /** Посты: теги */
    val tagsPostsViewMode: PostsViewMode = DEFAULT_POSTS_VIEW_MODE,
    /** Посты: поиск */
    val searchPostsViewMode: PostsViewMode = DEFAULT_POSTS_VIEW_MODE,

    /** Предлагать рандомных авторов */
    val suggestRandomAuthors: Boolean = DEFAULT_SUGGEST_RANDOM_AUTHORS,

    /** Способ перевода */
    val translateTarget: TranslateTarget = DEFAULT_TRANSLATE_TARGET,

    /** Где показывать кнопку "рандом" */
    val randomButtonPlacement: RandomButtonPlacement = DEFAULT_RANDOM_BUTTON_PLACEMENT,

    /** Язык, на который переводим ("" = системный) */
    val translateLanguageTag: String = DEFAULT_TRANSLATE_LANGUAGE_TAG,

    /** Формат даты в приложении */
    val dateFormatMode: DateFormatMode = DEFAULT_DATE_FORMAT_MODE,
) {
    companion object {
        val DEFAULT_CREATORS_VIEW_MODE = CreatorViewMode.LIST
        val DEFAULT_POSTS_VIEW_MODE = PostsViewMode.GRID
        const val DEFAULT_SUGGEST_RANDOM_AUTHORS = false
        val DEFAULT_TRANSLATE_TARGET = TranslateTarget.APP
        val DEFAULT_RANDOM_BUTTON_PLACEMENT = RandomButtonPlacement.SEARCH_BAR
        const val DEFAULT_TRANSLATE_LANGUAGE_TAG = "" // системный
        val DEFAULT_DATE_FORMAT_MODE = DateFormatMode.DD_MM_YYYY
    }
}