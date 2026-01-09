package su.afk.kemonos.creatorPost.presenter

import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import su.afk.kemonos.common.error.IErrorHandlerUseCase
import su.afk.kemonos.common.error.storage.RetryStorage
import su.afk.kemonos.common.error.toFavoriteToastBar
import su.afk.kemonos.common.presenter.baseViewModel.BaseViewModel
import su.afk.kemonos.common.shared.ShareLinkBuilder
import su.afk.kemonos.common.shared.ShareTarget
import su.afk.kemonos.common.translate.IOpenTranslateUseCase
import su.afk.kemonos.creatorPost.domain.model.video.VideoInfoState
import su.afk.kemonos.creatorPost.domain.useCase.GetCommentsUseCase
import su.afk.kemonos.creatorPost.domain.useCase.GetPostUseCase
import su.afk.kemonos.creatorPost.domain.useCase.GetVideoInfoUseCase
import su.afk.kemonos.creatorPost.navigation.CreatorPostDest
import su.afk.kemonos.creatorPost.presenter.delegates.LikeDelegate
import su.afk.kemonos.creatorPost.presenter.delegates.NavigateDelegates
import su.afk.kemonos.creatorProfile.api.IGetProfileUseCase
import su.afk.kemonos.preferences.IGetCurrentSiteRootUrlUseCase

internal class CreatorPostViewModel @AssistedInject constructor(
    @Assisted private val dest: CreatorPostDest.CreatorPost,
    private val getCommentsUseCase: GetCommentsUseCase,
    private val getPostUseCase: GetPostUseCase,
    private val getProfileUseCase: IGetProfileUseCase,
    private val getCurrentSiteRootUrlUseCase: IGetCurrentSiteRootUrlUseCase,
    private val getVideoInfo: GetVideoInfoUseCase,
    private val likeDelegate: LikeDelegate,
    private val navigateDelegates: NavigateDelegates,
    private val openTranslateUseCase: IOpenTranslateUseCase,
    override val errorHandler: IErrorHandlerUseCase,
    override val retryStorage: RetryStorage,
) : BaseViewModel<CreatorPostState>(CreatorPostState()) {

    private val _effect = Channel<CreatorPostEffect>(Channel.BUFFERED)
    val effect = _effect.receiveAsFlow()

    @AssistedFactory
    interface Factory {
        fun create(dest: CreatorPostDest.CreatorPost): CreatorPostViewModel
    }

    override fun onRetry() {
        setState { copy(loading = true) }
        loadingPost()
    }

    init {
        setState {
            copy(
                service = dest.service,
                id = dest.id,
                postId = dest.postId,
                showBarCreator = dest.showBarCreator
            )
        }

        loadingPost()
    }

    fun loadingPost() = viewModelScope.launch {
        setState { copy(loading = true) }

        val postDeferred = async { getPostUseCase(currentState.service, currentState.id, currentState.postId) }
        val commentsDeferred = async { getCommentsUseCase(currentState.service, currentState.id, currentState.postId) }

        /** шапка профиля не всегда нужна */
        val profileDeferred = if (currentState.showBarCreator) {
            async {
                getProfileUseCase(service = currentState.service, id = currentState.id)
            }
        } else {
            null
        }

        val post = postDeferred.await()
        val comments = commentsDeferred.await()
        val profile = profileDeferred?.await()

        setState {
            copy(
                loading = false,
                post = post,
                commentDomains = comments,
                profile = profile
            )
        }

        isPostFavorite()
    }

    /** Получить/создать flow для конкретного видео и стартануть загрузку при необходимости */
    private val videoInfoFlows = mutableMapOf<String, StateFlow<VideoInfoState>>()

    fun observeVideoInfo(url: String, name: String): StateFlow<VideoInfoState> {
        val key = url
        return videoInfoFlows.getOrPut(key) {
            kotlinx.coroutines.flow.flow {
                emit(VideoInfoState.Loading)
                val info = getVideoInfo(url, name)
                emit(VideoInfoState.Success(info))
            }.catch { e ->
                emit(VideoInfoState.Error(e))
            }.stateIn(
                scope = viewModelScope,
                started = kotlinx.coroutines.flow.SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
                initialValue = VideoInfoState.Loading
            )
        }
    }

    /** Избранное */
    fun onFavoriteClick() = viewModelScope.launch {
        if (currentState.favoriteActionLoading) return@launch

        val wasFavorite = currentState.isFavorite
        setState { copy(favoriteActionLoading = true) }

        val result = likeDelegate.onFavoriteClick(
            isFavorite = wasFavorite,
            post = currentState.post,
            service = currentState.service,
            creatorId = currentState.id,
            postId = currentState.postId
        )

        result
            .onSuccess {
                setState { copy(isFavorite = !wasFavorite) }
            }
            .onFailure { t ->
                val errorMessage = errorHandler.parse(t).toFavoriteToastBar()
                _effect.trySend(CreatorPostEffect.ShowToast(errorMessage))
            }
        setState { copy(favoriteActionLoading = false) }
    }

    /** Проверит в избранном ли пост */
    fun isPostFavorite() = viewModelScope.launch {
        val isShowAvailable = likeDelegate.postIsAvailableLike()
        if (isShowAvailable) {
            val favorite = likeDelegate.isPostFavorite(
                service = currentState.service,
                creatorId = currentState.id,
                postId = currentState.postId,
            )
            setState { copy(isFavorite = favorite) }
        }
        setState { copy(isFavoriteShowButton = isShowAvailable) }
    }

    /** навиагция на профиль автора */
    fun navigateToCreatorProfile() = navigateDelegates.navigateToCreatorProfile(currentState.id, currentState.service)

    fun navigateOpenImage(originalUrl: String) = navigateDelegates.navigateOpenImage(originalUrl)

    /** Копирование в буфер */
    fun copyPostLink() {
        val url = ShareLinkBuilder.build(
            ShareTarget.Post(
                siteRoot = getCurrentSiteRootUrlUseCase(),
                service = currentState.service,
                userId = currentState.id,
                postId = currentState.postId
            )
        )
        _effect.trySend(CreatorPostEffect.CopyPostLink(url))
    }
}