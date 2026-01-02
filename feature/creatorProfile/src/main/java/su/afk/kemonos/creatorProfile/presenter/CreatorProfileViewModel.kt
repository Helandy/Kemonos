package su.afk.kemonos.creatorProfile.presenter

import android.content.Context
import android.util.Log
import androidx.paging.cachedIn
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import su.afk.kemonos.common.domain.useCase.GetProfileUseCase
import su.afk.kemonos.common.error.IErrorHandlerUseCase
import su.afk.kemonos.common.error.storage.RetryStorage
import su.afk.kemonos.common.presenter.baseViewModel.BaseViewModel
import su.afk.kemonos.common.shared.ShareActions
import su.afk.kemonos.common.shared.ShareLinkBuilder
import su.afk.kemonos.common.shared.ShareTarget
import su.afk.kemonos.creatorProfile.api.domain.models.profileLinks.ProfileLink
import su.afk.kemonos.creatorProfile.domain.paging.GetProfilePostsPagingUseCase
import su.afk.kemonos.creatorProfile.navigation.CreatorDest
import su.afk.kemonos.creatorProfile.presenter.delegates.LikeDelegate
import su.afk.kemonos.creatorProfile.presenter.delegates.LoadingTabsContent
import su.afk.kemonos.creatorProfile.presenter.delegates.NavigationDelegate
import su.afk.kemonos.creatorProfile.presenter.model.ProfileTab
import su.afk.kemonos.domain.domain.models.PostDomain
import su.afk.kemonos.domain.domain.models.Tag
import su.afk.kemonos.navigation.NavigationManager
import su.afk.kemonos.preferences.GetKemonoRootUrlUseCase
import su.afk.kemonos.preferences.IGetCurrentSiteRootUrlUseCase

internal class CreatorProfileViewModel @AssistedInject constructor(
    @Assisted private val dest: CreatorDest.CreatorProfile,
    private val errorHandlerUseCase: IErrorHandlerUseCase,
    private val getProfileUseCase: GetProfileUseCase,
    private val getKemonoRootUrlUseCase: GetKemonoRootUrlUseCase,
    private val getCurrentSiteRootUrlUseCase: IGetCurrentSiteRootUrlUseCase,
    private val likeDelegate: LikeDelegate,
    private val navigationDelegate: NavigationDelegate,
    private val loadingTabsContent: LoadingTabsContent,
    private val getProfilePostsPagingUseCase: GetProfilePostsPagingUseCase,
    private val navManager: NavigationManager,
    override val errorHandler: IErrorHandlerUseCase,
    override val retryStorage: RetryStorage,
) : BaseViewModel<CreatorProfileState>(CreatorProfileState()) {
    private val _effect = Channel<CreatorProfileEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    private var searchJob: Job? = null

    @AssistedFactory
    interface Factory {
        fun create(dest: CreatorDest.CreatorProfile): CreatorProfileViewModel
    }

    override fun onRetry() {
        setState { copy(loading = true) }
        getProfileInfo()
        setTabsToProfile()
        isCreatorFavorite()
        /** Загрузка постов на странице */
        loadProfileAndPosts()
    }

    init {
        setState {
            copy(
                service = dest.service,
                id = dest.id,
                currentTag = null
            )
        }

        if (!isDiscordProfile(dest.service)) {
            getProfileInfo()
            setTabsToProfile()
            isCreatorFavorite()
            /** Загрузка постов на странице */
            loadProfileAndPosts()
        }
    }

    /** Если профиль из дискорда — открываем браузер */
    private fun isDiscordProfile(service: String): Boolean {
        if (service != "discord") return false

        val baseUrl = getKemonoRootUrlUseCase()
        val url = "$baseUrl/$service/server/${currentState.id}"

        setState { copy(isDiscordProfile = true) }

        _effect.trySend(CreatorProfileEffect.OpenUrl(url))

        Log.e("super", "url $url")
        return true
    }

    /** Получение информации о Профиле */
    fun getProfileInfo() = viewModelScope.launch {
        val profile = getProfileUseCase(currentState.service, currentState.id)

        setState {
            copy(
                profile = profile,
                countDm = profile?.dmCount,
                countPost = profile?.postCount,
            )
        }
    }

    /** Загрузка постов на странице */
    fun loadProfileAndPosts() = viewModelScope.launch {
        setState {
            copy(
                profilePosts = getProfilePostsPagingUseCase(
                    service = currentState.service,
                    id = currentState.id,
                    tag = currentState.currentTag?.tag,
                    search = currentState.searchText
                ).cachedIn(viewModelScope),
                loading = false,
            )
        }
    }


    /** Смена вкладки */
    fun onTabChanged(tab: ProfileTab) = setState { copy(selectedTab = tab) }

    /** Какие вкладки отображать */
    fun setTabsToProfile() = viewModelScope.launch {
        /** базовый набор табов */
        val tabs = mutableListOf(ProfileTab.POSTS)
        /** Лс */
        currentState.countDm?.let { if (it > 0) tabs.add(ProfileTab.DMS) }

        setState { copy(showTabs = tabs) }

        val service = currentState.service
        val id = currentState.id

        val checks = listOf(
            async {
                loadingTabsContent.checkDms(
                    countDm = currentState.countDm,
                    setState = ::setState,
                    service = service,
                    id = id
                )
            },
            async {
                loadingTabsContent.checkFanCard(
                    setState = ::setState,
                    service = service,
                    id = id
                )
            },
            async {
                loadingTabsContent.checkAnnouncements(
                    setState = ::setState,
                    service = service,
                    id = id
                )
            },
            async {
                loadingTabsContent.checkTags(
                    setState = ::setState,
                    service = service,
                    id = id
                )
            },
            async {
                loadingTabsContent.checkLinks(
                    setState = ::setState,
                    service = service,
                    id = id
                )
            }
        )
        checks.awaitAll()
    }

    /** search on tag */
    fun clickTag(tag: Tag) = viewModelScope.launch {
        setState {
            copy(
                selectedTab = ProfileTab.POSTS,
                currentTag = tag,
                profilePosts = getProfilePostsPagingUseCase(
                    service = currentState.service,
                    id = currentState.id,
                    tag = tag.tag,
                    search = null
                ).cachedIn(viewModelScope),
            )
        }
    }

    /** Сброс выбранного тега с запросом постов и страниц */
    fun clearTag() {
        setState {
            copy(
                selectedTab = ProfileTab.POSTS,
                currentTag = null,
                profilePosts = getProfilePostsPagingUseCase(
                    service = currentState.service,
                    id = currentState.id,
                    tag = null,
                    search = null
                ).cachedIn(viewModelScope),
            )
        }
    }

    /** Поиск по тексту */
    fun setSearchText(text: String) {
        setState { copy(searchText = text) }

        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(2_000)
            setState {
                copy(
                    selectedTab = ProfileTab.POSTS,
                    profilePosts = getProfilePostsPagingUseCase(
                        service = currentState.service,
                        id = currentState.id,
                        tag = null,
                        search = text
                    ).cachedIn(viewModelScope),
                )
            }
        }
    }

    /** скрыть поиск */
    fun setSearchVisible(visible: Boolean) = setState { copy(isSearchVisible = visible) }

    /** показать-скрыть поиск */
    fun toggleSearch() = setState { copy(isSearchVisible = !currentState.isSearchVisible) }

    /** избранное */
    fun onFavoriteClick() = viewModelScope.launch {
        val result = likeDelegate.onFavoriteClick(
            isFavorite = currentState.isFavorite,
            service = currentState.service,
            id = currentState.id
        )
        if (result) setState { copy(isFavorite = !currentState.isFavorite) }
    }

    /** проверит в избранном ли автор */
    fun isCreatorFavorite() = viewModelScope.launch {
        val isShowAvailable = likeDelegate.creatorIsAvailableLike()
        if (isShowAvailable) {
            val favorite = likeDelegate.isCreatorFavorite(
                service = currentState.service,
                id = currentState.id
            )
            setState { copy(isFavorite = favorite) }
        }
        setState { copy(isFavoriteShowButton = isShowAvailable) }
    }

    /**  navigate to open funcard image */
    fun navigateToOpenImage(originalUrl: String) = navigationDelegate.navigateToOpenImage(originalUrl)

    /** navigate to Link Profile */
    fun navigateToLinkProfile(creator: ProfileLink) = navigationDelegate.navigateToLinkProfile(creator)

    /** Открытие поста */
    fun navigateToPost(post: PostDomain) = navigationDelegate.navigateToPost(post)

    fun parseError(t: Throwable) = errorHandlerUseCase.parse(t)

    fun back() {
        navManager.back()
    }

    /** Копирование в буфер todo убрать context */
    fun copyProfileLink(context: Context) {
        val url = ShareLinkBuilder.build(
            ShareTarget.Profile(
                siteRoot = getCurrentSiteRootUrlUseCase(),
                service = currentState.service,
                userId = currentState.id
            )
        )
        ShareActions.copyToClipboard(context, "Profile link", url)
    }
}

