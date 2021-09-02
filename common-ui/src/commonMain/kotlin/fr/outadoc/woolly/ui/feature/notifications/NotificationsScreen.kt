package fr.outadoc.woolly.ui.feature.notifications

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import fr.outadoc.mastodonk.api.entity.Attachment
import fr.outadoc.mastodonk.api.entity.Status
import fr.outadoc.woolly.common.feature.notifications.viewmodel.NotificationsViewModel

@Composable
fun NotificationsScreen(
    viewModel: NotificationsViewModel,
    insets: PaddingValues = PaddingValues(),
    listState: LazyListState,
    onStatusClick: (Status) -> Unit = {},
    onAttachmentClick: (Attachment) -> Unit = {}
) {
    NotificationList(
        insets = insets,
        notificationFlow = viewModel.pagingData,
        lazyListState = listState,
        onStatusClick = onStatusClick,
        onAttachmentClick = onAttachmentClick
    )
}
