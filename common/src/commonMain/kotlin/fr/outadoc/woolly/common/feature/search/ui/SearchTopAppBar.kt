package fr.outadoc.woolly.common.feature.search.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AppBarDefaults
import androidx.compose.material.DrawerState
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.LocalContentColor
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.ProvideTextStyle
import androidx.compose.material.Surface
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.primarySurface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import fr.outadoc.woolly.common.feature.search.SearchScreenResources
import fr.outadoc.woolly.common.feature.search.SearchSubScreen
import fr.outadoc.woolly.common.feature.search.viewmodel.SearchViewModel
import fr.outadoc.woolly.common.navigation.TopAppBarWithMenu
import org.kodein.di.compose.LocalDI
import org.kodein.di.instance

@Composable
fun SearchTopAppBar(
    currentSubScreen: SearchSubScreen,
    onCurrentSubScreenChanged: (SearchSubScreen) -> Unit,
    drawerState: DrawerState?
) {
    val di = LocalDI.current
    val vm by di.instance<SearchViewModel>()
    val state by vm.state.collectAsState()

    val textStyle = LocalTextStyle.current

    Surface(
        color = MaterialTheme.colors.primarySurface,
        elevation = AppBarDefaults.TopAppBarElevation
    ) {
        Column {
            TopAppBarWithMenu(
                backgroundColor = MaterialTheme.colors.primarySurface,
                title = {
                    ProvideTextStyle(value = textStyle) {
                        SearchTextField(
                            searchTerm = state.query,
                            onSearchTermChanged = {
                                vm.onSearchTermChanged(it)
                            }
                        )
                    }
                },
                drawerState = drawerState,
                elevation = 0.dp
            )

            if (state.query.isNotEmpty()) {
                SearchTabRow(currentSubScreen, onCurrentSubScreenChanged)
            }
        }
    }
}

@Composable
fun SearchTabRow(
    currentSubScreen: SearchSubScreen,
    onCurrentSubScreenChanged: (SearchSubScreen) -> Unit,
) {
    val di = LocalDI.current
    val searchRes by di.instance<SearchScreenResources>()

    val tabs = listOf(
        SearchSubScreen.Statuses,
        SearchSubScreen.Accounts,
        SearchSubScreen.Hashtags
    )

    TabRow(selectedTabIndex = tabs.indexOf(currentSubScreen)) {
        tabs.forEach { screen ->
            Tab(
                modifier = Modifier.height(48.dp),
                selected = currentSubScreen == screen,
                onClick = {
                    onCurrentSubScreenChanged(screen)
                }
            ) {
                Text(text = searchRes.getScreenTitle(screen))
            }
        }
    }
}

@Composable
fun SearchTextField(
    searchTerm: String,
    onSearchTermChanged: (String) -> Unit,
) {
    OutlinedTextField(
        value = searchTerm,
        onValueChange = { term -> onSearchTermChanged(term) },
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 4.dp),
        singleLine = true,
        leadingIcon = {
            Icon(Icons.Default.Search, "Search")
        },
        placeholder = { Text("Search for something…") },
        trailingIcon = {
            if (searchTerm.isNotEmpty()) {
                IconButton(onClick = { onSearchTermChanged("") }) {
                    Icon(Icons.Default.Clear, "Clear search")
                }
            }
        },
        colors = TextFieldDefaults.outlinedTextFieldColors(
            cursorColor = LocalContentColor.current,
            leadingIconColor = LocalContentColor.current,
            placeholderColor = LocalContentColor.current,
            trailingIconColor = LocalContentColor.current,
            focusedBorderColor = Color.Transparent,
            unfocusedBorderColor = Color.Transparent
        )
    )
}
