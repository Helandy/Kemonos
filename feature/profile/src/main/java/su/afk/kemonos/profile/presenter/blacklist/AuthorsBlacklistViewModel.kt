package su.afk.kemonos.profile.presenter.blacklist

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import su.afk.kemonos.creatorProfile.api.ICreatorProfileNavigator
import su.afk.kemonos.error.error.IErrorHandlerUseCase
import su.afk.kemonos.error.error.storage.RetryStorage
import su.afk.kemonos.navigation.NavigationManager
import su.afk.kemonos.preferences.domainResolver.IDomainResolver
import su.afk.kemonos.preferences.domainResolver.selectedSiteByService
import su.afk.kemonos.preferences.site.ISelectedSiteUseCase
import su.afk.kemonos.preferences.site.setSiteAndAwait
import su.afk.kemonos.preferences.ui.IUiSettingUseCase
import su.afk.kemonos.profile.presenter.blacklist.AuthorsBlacklistState.*
import su.afk.kemonos.storage.api.repository.blacklist.IStoreBlacklistedAuthorsRepository
import su.afk.kemonos.ui.presenter.baseViewModel.BaseViewModelNew
import javax.inject.Inject

@HiltViewModel
internal class AuthorsBlacklistViewModel @Inject constructor(
    private val navManager: NavigationManager,
    private val creatorProfileNavigator: ICreatorProfileNavigator,
    private val blacklistedAuthorsRepository: IStoreBlacklistedAuthorsRepository,
    private val domainResolver: IDomainResolver,
    private val selectedSiteUseCase: ISelectedSiteUseCase,
    private val uiSetting: IUiSettingUseCase,
    override val errorHandler: IErrorHandlerUseCase,
    override val retryStorage: RetryStorage,
) : BaseViewModelNew<State, Event, Effect>() {
    override fun createInitialState(): State = State()

    init {
        observeUiSetting()
        observeBlacklist()
    }

    override fun onEvent(event: Event) {
        when (event) {
            Event.Back -> navManager.back()
            is Event.QueryChanged -> setState { copy(query = event.value) }
            is Event.OpenProfile -> openProfile(event.service, event.creatorId)
            is Event.RequestRemoveAuthor -> setState { copy(pendingRemoveAuthor = event.author) }
            Event.DismissRemoveAuthor -> setState { copy(pendingRemoveAuthor = null) }
            Event.ConfirmRemoveAuthor -> confirmRemoveAuthor()
        }
    }

    private fun observeBlacklist() {
        blacklistedAuthorsRepository.observeAll()
            .onEach { items ->
                setState {
                    copy(
                        items = items
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    private fun observeUiSetting() {
        uiSetting.prefs.distinctUntilChanged()
            .onEach { model ->
                setState { copy(uiSettingModel = model) }
            }
            .launchIn(viewModelScope)
    }

    private fun removeAuthor(service: String, creatorId: String) = viewModelScope.launch {
        blacklistedAuthorsRepository.remove(service = service, creatorId = creatorId)
    }

    private fun confirmRemoveAuthor() = viewModelScope.launch {
        val pending = currentState.pendingRemoveAuthor ?: return@launch
        removeAuthor(service = pending.service, creatorId = pending.creatorId)
        setState { copy(pendingRemoveAuthor = null) }
    }

    private fun openProfile(service: String, creatorId: String) = viewModelScope.launch {
        val targetSite = domainResolver.selectedSiteByService(service)
        selectedSiteUseCase.setSiteAndAwait(targetSite)

        navManager.navigate(
            creatorProfileNavigator.getCreatorProfileDest(
                service = service,
                id = creatorId,
                isFresh = false
            )
        )
    }
}
