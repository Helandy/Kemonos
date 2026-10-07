package su.afk.kemonos.creatorPost.presenter.delegates

import su.afk.kemonos.creatorPost.presenter.CreatorPostState.Effect
import su.afk.kemonos.creatorPost.presenter.CreatorPostState.State
import su.afk.kemonos.creatorPost.presenter.helper.collectDownloadAllItems
import su.afk.kemonos.download.api.IDownloadUtil
import su.afk.kemonos.preferences.domainResolver.IDomainResolver
import su.afk.kemonos.preferences.domainResolver.mediaUrlSchemeByService
import javax.inject.Inject

internal class PostDownloadDelegate @Inject constructor(
    private val downloadUtil: IDownloadUtil,
    private val domainResolver: IDomainResolver,
) {
    /** Постановка одной загрузки в системный DownloadManager + тост-эффект */
    suspend fun download(
        url: String,
        fileName: String?,
        state: State,
        sendEffect: (Effect) -> Unit,
    ) {
        downloadUtil.enqueueSystemDownload(
            url = url,
            fileName = fileName,
            service = state.service,
            creatorName = state.profile?.name,
            postId = state.postId,
            postTitle = state.post?.post?.title
        )
        sendEffect(Effect.DownloadToast(fileName.orEmpty()))
    }

    /** Массовая загрузка всех доступных вложений/медиа поста */
    suspend fun downloadAll(
        state: State,
        sendEffect: (Effect) -> Unit,
    ) {
        val post = state.post ?: return
        val allItems = post.collectDownloadAllItems(
            fallbackBaseUrl = domainResolver.fileBaseUrlByService(state.service),
            mediaUrlScheme = domainResolver.mediaUrlSchemeByService(state.service),
        )

        allItems.forEach { item ->
            download(url = item.url, fileName = item.fileName, state = state, sendEffect = sendEffect)
        }
    }
}
