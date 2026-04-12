package su.afk.kemonos.creatorPost.presenter.delegates

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import su.afk.kemonos.creatorPost.api.domain.model.PostContentDomain
import su.afk.kemonos.creatorPost.domain.useCase.GetCommentsUseCase
import su.afk.kemonos.creatorPost.domain.useCase.GetPostUseCase
import su.afk.kemonos.creatorPost.presenter.model.LoadRequest
import su.afk.kemonos.creatorPost.presenter.model.LoadedPostData
import su.afk.kemonos.creatorPost.presenter.model.LoadedRevisionData
import su.afk.kemonos.creatorProfile.api.IGetProfileUseCase
import su.afk.kemonos.preferences.IGetCurrentSiteRootUrlUseCase
import su.afk.kemonos.ui.presenter.androidView.cleanDuplicatedMediaFromContent
import su.afk.kemonos.ui.presenter.androidView.clearHtml
import su.afk.kemonos.ui.presenter.androidView.htmlToBlocks
import su.afk.kemonos.ui.presenter.androidView.model.PostBlock
import javax.inject.Inject

internal class PostLoadDelegate @Inject constructor(
    private val getPostUseCase: GetPostUseCase,
    private val getCommentsUseCase: GetCommentsUseCase,
    private val getProfileUseCase: IGetProfileUseCase,
    private val getCurrentSiteRootUrlUseCase: IGetCurrentSiteRootUrlUseCase,
) {

    suspend fun load(request: LoadRequest): LoadedPostData = coroutineScope {
        val postDeferred = async { getPostUseCase(request.service, request.creatorId, request.postId) }
        val commentsDeferred = async {
            if (request.showComments) {
                runCatching {
                    getCommentsUseCase(request.service, request.creatorId, request.postId)
                }.getOrElse {
                    emptyList()
                }
            } else {
                emptyList()
            }
        }
        val profileDeferred = async { getProfileUseCase(service = request.service, id = request.creatorId) }

        val sourcePost = postDeferred.await()
        val selectedRevisionId: Int? = null
        val resolvedPost = sourcePost?.toResolvedPost(selectedRevisionId)

        LoadedPostData(
            sourcePost = sourcePost,
            resolvedPost = resolvedPost,
            comments = commentsDeferred.await(),
            profile = profileDeferred.await(),
            selectedRevisionId = selectedRevisionId,
            revisionIds = sourcePost?.buildRevisionSelectorIds().orEmpty(),
            showButtonTranslate = shouldShowTranslate(resolvedPost),
            contentBlocks = buildContentBlocks(resolvedPost),
        )
    }

    suspend fun loadRevision(sourcePost: PostContentDomain, revisionId: Int?): LoadedRevisionData {
        val resolvedPost = sourcePost.resolveRevisionPost(revisionId)
        return LoadedRevisionData(
            resolvedPost = resolvedPost,
            showButtonTranslate = shouldShowTranslate(resolvedPost),
            contentBlocks = buildContentBlocks(resolvedPost),
        )
    }

    private suspend fun buildContentBlocks(post: PostContentDomain?): List<PostBlock> {
        val mediaRefs = post?.collectMediaRefsForDedup().orEmpty()
        val siteBaseUrl = getCurrentSiteRootUrlUseCase()
        val cleanContent = withContext(Dispatchers.Default) {
            cleanDuplicatedMediaFromContent(
                html = post?.post?.content.orEmpty().take(MAX_HTML_CHARS),
                attachmentPaths = mediaRefs,
            )
        }
        return withContext(Dispatchers.Default) {
            htmlToBlocks(cleanContent, siteBaseUrl)
        }
    }

    private fun shouldShowTranslate(post: PostContentDomain?): Boolean {
        return post?.post?.content?.clearHtml()?.isNotBlank() == true
    }

    private fun PostContentDomain.buildRevisionSelectorIds(): List<Int?> {
        if (revisions.size <= 1) return emptyList()

        val ids = revisions
            .filter { it.backendRevisionId != null }
            .map { it.revisionId }
            .distinct()
        if (ids.isEmpty()) return emptyList()
        return listOf(null) + ids
    }

    private fun PostContentDomain.toResolvedPost(selectedRevisionId: Int?): PostContentDomain {
        if (selectedRevisionId == null) return this
        val selected = revisions.firstOrNull { it.revisionId == selectedRevisionId } ?: return this
        val selectedAttachments = selected.post.attachments.ifEmpty { attachments }
        return copy(
            post = selected.post,
            attachments = selectedAttachments,
        )
    }

    private suspend fun PostContentDomain.resolveRevisionPost(selectedRevisionId: Int?): PostContentDomain {
        if (selectedRevisionId == null) return this

        val localResolved = toResolvedPost(selectedRevisionId)
        val backendRevisionId = revisions
            .firstOrNull { it.revisionId == selectedRevisionId }
            ?.backendRevisionId
            ?: return localResolved

        val revisionFromApi = getPostUseCase.getRevision(
            service = post.service,
            id = post.userId,
            postId = post.id,
            revisionId = backendRevisionId,
        ) ?: return localResolved

        return revisionFromApi.copy(
            // Backend revision endpoint can return current post timestamps.
            // Keep textual/date fields from revision payload in props.revisions.
            post = localResolved.post,
            revisions = revisions,
        )
    }

    private fun PostContentDomain.collectMediaRefsForDedup(): List<String> = buildList {
        addAll(attachments.map { it.path })
        addAll(previews.mapNotNull { it.path })
        addAll(previews.mapNotNull { it.url })
        post.file?.path?.let(::add)
        addAll(post.attachments.map { it.path })
    }.filter { it.isNotBlank() }

    private companion object {
        const val MAX_HTML_CHARS = 100_000
    }
}
