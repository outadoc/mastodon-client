package fr.outadoc.woolly.common.feature.mainrouter.component

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.RouterState
import com.arkivanov.decompose.pop
import com.arkivanov.decompose.push
import com.arkivanov.decompose.replaceCurrent
import com.arkivanov.decompose.router
import com.arkivanov.decompose.value.Value
import com.arkivanov.decompose.value.operator.map
import fr.outadoc.mastodonk.api.entity.Attachment
import fr.outadoc.mastodonk.api.entity.Status
import fr.outadoc.woolly.common.feature.media.toAppImage
import fr.outadoc.woolly.common.screen.AppScreen
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import org.kodein.di.DirectDI
import org.kodein.di.factory

class MainRouterComponent(
    componentContext: ComponentContext,
    private val directDI: DirectDI
) : ComponentContext by componentContext {

    private val router = router<AppScreen, Content>(
        initialConfiguration = { AppScreen.HomeTimeline },
        handleBackButton = true,
        childFactory = ::createChild
    )

    val routerState: Value<RouterState<AppScreen, Content>> = router.state

    sealed class Event {
        data class OpenUri(val uri: String) : Event()
    }

    private val _events = MutableSharedFlow<Event>()
    val events = _events.asSharedFlow()

    val shouldDisplayBackButton: Value<Boolean>
        get() = routerState.map { it.backStack.isNotEmpty() }

    val shouldDisplayComposeButton: Value<Boolean>
        get() = routerState.map { it.backStack.isEmpty() }

    fun onAttachmentClick(attachment: Attachment) {
        when (attachment) {
            is Attachment.Image -> router.push(
                AppScreen.ImageViewer(
                    image = attachment.toAppImage()
                )
            )
            else -> _events.tryEmit(Event.OpenUri(attachment.url))
        }
    }

    fun onStatusClick(status: Status) {
        router.push(
            AppScreen.StatusDetails(statusId = status.statusId)
        )
    }

    fun onBackPressed() {
        router.pop()
    }

    fun onComposeStatusClicked() {
        router.push(AppScreen.StatusComposer)
    }

    fun onComposerDismissed() {
        router.pop()
    }

    fun onScreenSelected(target: AppScreen) {
        val activeContent = routerState.value.activeChild.instance
        if (target == activeContent.configuration) {
            (activeContent.component as? ScrollableComponent)
                ?.scrollToTop(activeContent.configuration)
        }

        router.replaceCurrent(target)
    }

    private fun createChild(
        configuration: AppScreen,
        componentContext: ComponentContext
    ): Content = when (configuration) {
        is AppScreen.HomeTimeline -> Content.HomeTimeline(
            configuration = configuration,
            component = createComponent(componentContext)
        )
        is AppScreen.PublicTimeline -> Content.PublicTimeline(
            configuration = configuration,
            component = createComponent(componentContext)
        )
        is AppScreen.Notifications -> Content.Notifications(
            configuration = configuration,
            component = createComponent(componentContext)
        )
        is AppScreen.Search -> Content.Search(
            configuration = configuration,
            component = createComponent(componentContext)
        )
        is AppScreen.Account -> Content.Account(
            configuration = configuration,
            component = createComponent(componentContext)
        )
        is AppScreen.Bookmarks -> Content.Bookmarks(
            configuration = configuration,
            component = createComponent(componentContext)
        )
        is AppScreen.Favourites -> Content.Favourites(
            configuration = configuration,
            component = createComponent(componentContext)
        )
        is AppScreen.StatusDetails -> Content.StatusDetails(
            configuration = configuration,
            component = createComponent(componentContext)
        )
        is AppScreen.ImageViewer -> Content.ImageViewer(
            configuration = configuration,
            component = createComponent(componentContext)
        )
        is AppScreen.StatusComposer -> Content.StatusComposer(
            configuration = configuration,
            component = createComponent(componentContext)
        )
    }

    private inline fun <reified T : Any> createComponent(componentContext: ComponentContext): T {
        return directDI.factory<ComponentContext, T>()(componentContext)
    }
}
