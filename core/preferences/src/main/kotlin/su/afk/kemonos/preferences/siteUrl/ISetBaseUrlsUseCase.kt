package su.afk.kemonos.preferences.siteUrl

interface ISetBaseUrlsUseCase {
    suspend operator fun invoke(
        kemonoUrl: String,
        coomerUrl: String,
        pawchiveUrl: String,
        pawchiveImageHostOverride: String? = null,
        pawchiveFileHostOverride: String? = null,
    )
}
