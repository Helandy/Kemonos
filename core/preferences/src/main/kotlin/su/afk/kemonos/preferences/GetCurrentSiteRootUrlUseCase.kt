package su.afk.kemonos.preferences

import su.afk.kemonos.preferences.site.ISelectedSiteUseCase
import javax.inject.Inject

interface IGetCurrentSiteRootUrlUseCase {
    operator fun invoke(): String
}

internal class GetCurrentSiteRootUrlUseCase @Inject constructor(
    private val selectedSiteUseCase: ISelectedSiteUseCase,
    private val getRootUrlUseCase: GetRootUrlUseCase,
) : IGetCurrentSiteRootUrlUseCase {

    override fun invoke(): String = getRootUrlUseCase(selectedSiteUseCase.getSite())
}
