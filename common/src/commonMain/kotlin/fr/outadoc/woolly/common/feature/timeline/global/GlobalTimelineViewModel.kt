package fr.outadoc.woolly.common.feature.timeline.global

import fr.outadoc.mastodonk.client.MastodonClient
import fr.outadoc.woolly.common.feature.timeline.AnnotatedStatus
import fr.outadoc.woolly.common.feature.timeline.PageState
import fr.outadoc.woolly.common.feature.timeline.StatusAnnotator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.withContext

class GlobalTimelineViewModel(
    private val mastodonClient: MastodonClient,
    private val statusAnnotator: StatusAnnotator
) {
    val state = MutableStateFlow<PageState<List<AnnotatedStatus>>>(PageState.Loading())

    suspend fun onPageOpen() {
        withContext(Dispatchers.IO) {
            val res = mastodonClient.timelines.getPublicTimeline()
            withContext(Dispatchers.Default) {
                state.emit(
                    PageState.Content(
                        res.contents.map { status ->
                            statusAnnotator.annotateStatus(status)
                        }
                    )
                )
            }
        }
    }
}
