package su.afk.kemonos.creatorPost.presenter.view.state

import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable

@Stable
internal class CreatorPostSectionState(
    previewsExpanded: Boolean = true,
    videosExpanded: Boolean = true,
    audioExpanded: Boolean = true,
    tagsExpanded: Boolean = true,
    attachmentsExpanded: Boolean = true,
    commentsExpanded: Boolean = true,
) {
    var previewsExpanded by mutableStateOf(previewsExpanded)
    var videosExpanded by mutableStateOf(videosExpanded)
    var audioExpanded by mutableStateOf(audioExpanded)
    var tagsExpanded by mutableStateOf(tagsExpanded)
    var attachmentsExpanded by mutableStateOf(attachmentsExpanded)
    var commentsExpanded by mutableStateOf(commentsExpanded)
}

@Composable
internal fun rememberCreatorPostSectionState(postId: String): CreatorPostSectionState {
    return rememberSaveable(postId, saver = CreatorPostSectionStateSaver) {
        CreatorPostSectionState()
    }
}

private val CreatorPostSectionStateSaver = listSaver<CreatorPostSectionState, Boolean>(
    save = {
        listOf(
            it.previewsExpanded,
            it.videosExpanded,
            it.audioExpanded,
            it.tagsExpanded,
            it.attachmentsExpanded,
            it.commentsExpanded,
        )
    },
    restore = { saved ->
        CreatorPostSectionState(
            previewsExpanded = saved[0],
            videosExpanded = saved[1],
            audioExpanded = saved[2],
            tagsExpanded = saved[3],
            attachmentsExpanded = saved[4],
            commentsExpanded = saved[5],
        )
    },
)
