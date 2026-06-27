package su.afk.kemonos.creators.presenter.model

import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.domain.models.creator.CreatorsSort

internal data class CreatorsFilters(
    val site: SelectedSite,
    val service: String?,
    val query: String,
    val sort: CreatorsSort,
    val ascending: Boolean,
)
