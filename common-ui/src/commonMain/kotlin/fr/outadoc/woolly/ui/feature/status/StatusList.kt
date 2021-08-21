package fr.outadoc.woolly.ui.feature.status

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.TabRowDefaults.Divider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemsIndexed
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import fr.outadoc.mastodonk.api.entity.Status
import fr.outadoc.woolly.common.feature.status.StatusAction
import fr.outadoc.woolly.ui.common.ListExtremityState
import fr.outadoc.woolly.ui.common.WoollyDefaults
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

@Composable
fun StatusList(
    modifier: Modifier = Modifier,
    insets: PaddingValues,
    statusFlow: Flow<PagingData<Status>>,
    lazyListState: LazyListState,
    maxContentWidth: Dp = WoollyDefaults.MaxContentWidth,
    onStatusAction: (StatusAction) -> Unit = {},
    onStatusClick: (Status) -> Unit = {}
) {
    // Periodically refresh timestamps
    var currentTime by remember { mutableStateOf(Clock.System.now()) }
    rememberCoroutineScope().launch(Dispatchers.Default) {
        while (isActive) {
            delay(1_000)
            currentTime = Clock.System.now()
        }
    }

    val lazyPagingItems = statusFlow.collectAsLazyPagingItems()

    SwipeRefresh(
        onRefresh = lazyPagingItems::refresh,
        state = rememberSwipeRefreshState(
            isRefreshing = lazyPagingItems.loadState.refresh == LoadState.Loading
        )
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center
        ) {
            LazyColumn(
                modifier = modifier.widthIn(max = maxContentWidth),
                state = lazyListState,
                contentPadding = insets
            ) {
                when (val state = lazyPagingItems.loadState.refresh) {
                    is LoadState.Error -> item {
                        ErrorScreen(
                            modifier = Modifier
                                .fillParentMaxSize()
                                .padding(16.dp),
                            error = state.error,
                            onRetry = lazyPagingItems::retry
                        )
                    }
                }

                item {
                    ListExtremityState(
                        state = lazyPagingItems.loadState.prepend,
                        onRetry = lazyPagingItems::retry
                    )
                }

                itemsIndexed(
                    items = lazyPagingItems,
                    key = { _, status -> status.statusId }
                ) { _, status ->
                    Column {
                        if (status != null) {
                            StatusOrBoost(
                                modifier = Modifier
                                    .clickable { onStatusClick(status) }
                                    .padding(
                                        top = 16.dp,
                                        start = 16.dp,
                                        end = 16.dp,
                                        bottom = 8.dp
                                    ),
                                status = status,
                                currentTime = currentTime,
                                onStatusAction = onStatusAction
                            )
                        } else {
                            StatusPlaceholder()
                        }

                        Divider(thickness = 1.dp)
                    }
                }

                item {
                    ListExtremityState(
                        state = lazyPagingItems.loadState.append,
                        onRetry = lazyPagingItems::retry
                    )
                }
            }
        }
    }
}