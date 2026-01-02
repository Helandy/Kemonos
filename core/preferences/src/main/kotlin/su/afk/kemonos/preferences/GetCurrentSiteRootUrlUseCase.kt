package su.afk.kemonos.preferences

import su.afk.kemonos.domain.SelectedSite
import su.afk.kemonos.preferences.site.ISelectedSiteUseCase
import javax.inject.Inject

interface IGetCurrentSiteRootUrlUseCase {
    operator fun invoke(): String
}

internal class GetCurrentSiteRootUrlUseCase @Inject constructor(
    private val selectedSiteUseCase: ISelectedSiteUseCase,
    private val getKemonoRootUrlUseCase: GetKemonoRootUrlUseCase,
    private val getCoomerRootUrlUseCase: GetCoomerRootUrlUseCase,
) : IGetCurrentSiteRootUrlUseCase {

    override fun invoke(): String = when (selectedSiteUseCase.getSite()) {
        SelectedSite.K -> getKemonoRootUrlUseCase()
        SelectedSite.C -> getCoomerRootUrlUseCase()
    }
}