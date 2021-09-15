package fr.outadoc.woolly.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import fr.outadoc.woolly.common.LoadState
import fr.outadoc.woolly.common.feature.authrouter.component.AuthRouterComponent
import fr.outadoc.woolly.common.feature.mainrouter.component.MainRouterComponent
import fr.outadoc.woolly.common.feature.preference.PreferenceRepository
import fr.outadoc.woolly.common.feature.theme.DisplayTheme
import fr.outadoc.woolly.common.feature.theme.ThemeProvider
import fr.outadoc.woolly.ui.common.WoollyTheme
import fr.outadoc.woolly.ui.feature.authrouter.AuthRouter
import fr.outadoc.woolly.ui.mainrouter.MainRouter
import kotlinx.coroutines.launch
import org.kodein.di.compose.instance

@Composable
fun WoollyApp(
    mainRouterComponent: MainRouterComponent,
    authRouterComponent: AuthRouterComponent,
    onFinishedLoading: () -> Unit = {}
) {
    val prefs by instance<PreferenceRepository>()
    val appPrefsState by prefs.preferences.collectAsState()
    val themeProvider by instance<ThemeProvider>()

    val scope = rememberCoroutineScope()

    when (val state = appPrefsState) {
        is LoadState.Loaded -> {
            LaunchedEffect(state) {
                onFinishedLoading()
            }

            val appPrefs = state.value
            val theme by themeProvider.theme.collectAsState()
            WoollyTheme(isDarkMode = theme == DisplayTheme.Dark) {
                if (appPrefs.authenticationState.activeAccount == null) {
                    AuthRouter(
                        component = authRouterComponent
                    )
                } else {
                    MainRouter(
                        component = mainRouterComponent,
                        preferredTheme = appPrefs.preferredTheme,
                        onColorSchemeChanged = { colorScheme ->
                            scope.launch {
                                prefs.updatePreferences { current ->
                                    current.copy(preferredTheme = colorScheme)
                                }
                            }
                        }
                    )
                }
            }
        }
        is LoadState.Loading -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
            }
        }
    }
}
