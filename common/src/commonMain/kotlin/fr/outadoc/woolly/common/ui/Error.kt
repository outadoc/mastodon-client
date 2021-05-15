package fr.outadoc.woolly.common.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import fr.outadoc.mastodonk.api.entity.Error
import fr.outadoc.mastodonk.client.MastodonApiException

@Composable
fun LazyItemScope.CenteredErrorMessage(error: Throwable? = null, onRetry: () -> Unit = {}) {
    Column(
        modifier = Modifier.fillParentMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ErrorScreen(
            error = error,
            onRetry = onRetry
        )
    }
}

@Composable
fun ErrorScreen(error: Throwable? = null, onRetry: () -> Unit = {}) {
    Text(
        "An error happened while loading.",
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.h5
    )

    ExceptionErrorMessage(error)

    Button(
        modifier = Modifier.padding(top = 16.dp),
        onClick = { onRetry() }
    ) {
        Text("Retry")
    }
}

@Composable
fun ExceptionErrorMessage(error: Throwable?) {
    val genericErrorMessage = error?.message
    val apiError = (error as? MastodonApiException)?.apiError

    if (apiError != null) {
        MastodonErrorMessage(apiError)
    } else if (genericErrorMessage != null) {
        GenericErrorMessage(genericErrorMessage)
    }

}

@Composable
fun MastodonErrorMessage(apiError: Error) {
    Text(
        apiError.error,
        modifier = Modifier.padding(top = 8.dp),
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.body1
    )

    val description = apiError.errorDescription
    if (description != null) {
        Text(
            description,
            modifier = Modifier.padding(top = 8.dp),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.body2
        )
    }
}

@Composable
fun GenericErrorMessage(message: String) {
    Text(
        message,
        modifier = Modifier.padding(top = 8.dp),
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.body1
    )
}
