package fr.outadoc.woolly.common.feature.bookmarks.viewmodel

import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.arkivanov.decompose.ComponentContext
import fr.outadoc.mastodonk.api.entity.Status
import fr.outadoc.mastodonk.paging.api.endpoint.accounts.getBookmarksSource
import fr.outadoc.woolly.common.feature.client.MastodonClientProvider
import fr.outadoc.woolly.common.feature.status.StatusAction
import fr.outadoc.woolly.common.feature.status.StatusActionRepository
import fr.outadoc.woolly.common.feature.status.StatusPagingRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

class BookmarksViewModel(
    componentContext: ComponentContext,
    viewModelScope: CoroutineScope,
    clientProvider: MastodonClientProvider,
    pagingConfig: PagingConfig,
    statusActionRepository: StatusActionRepository
) : ComponentContext by componentContext {

    private val pagingRepository = StatusPagingRepository(
        pagingConfig,
        clientProvider,
        statusActionRepository
    ) { client ->
        client.bookmarks.getBookmarksSource()
    }

    val bookmarksPagingItems: Flow<PagingData<Status>> =
        pagingRepository
            .pagingData
            .cachedIn(viewModelScope)

    fun onStatusAction(action: StatusAction) {
        pagingRepository.onStatusAction(action)
    }
}
