package su.afk.kemonos.ui.components.dm

data class DmUiItem(
    val hash: String,
    val content: String,
    val published: String,
    val creator: DmCreatorUi? = null,
)

data class DmCreatorUi(
    val service: String,
    val id: String,
    val name: String,
    val updated: String? = null,
)
