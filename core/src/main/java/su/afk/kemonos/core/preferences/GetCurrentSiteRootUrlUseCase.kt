package su.afk.kemonos.core.preferences

import su.afk.kemonos.core.api.domain.useCase.ISelectedSiteUseCase
import su.afk.kemonos.domain.SelectedSite
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