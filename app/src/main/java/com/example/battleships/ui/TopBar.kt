package com.example.battleships.ui

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import pt.isel.battleships.R

/**
 * Used to aggregate [TopBar] navigation handlers.
 */
data class NavigationHandlers(
    val onBackRequested: (() -> Unit)? = null,
    val onHistoryRequested: (() -> Unit)? = null,
    val onInfoRequested: (() -> Unit)? = null,
)

// Test tags for the TopBar navigation elements
const val NavigateBackTestTag = "NavigateBack"
const val NavigateToHistoryTestTag = "NavigateToHistory"
const val NavigateToInfoTestTag = "NavigateToInfo"

@Composable
fun TopBar(
    title: String = stringResource(id = R.string.app_name),
    navigation: NavigationHandlers = NavigationHandlers()
) {
    TopAppBar(
        title = { Text(text = title) },
        navigationIcon = {
            if (navigation.onBackRequested != null) {
                IconButton(
                    onClick = navigation.onBackRequested,
                    modifier = Modifier.testTag(NavigateBackTestTag)
                ) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = /* stringResource(id = R.string.top_bar_go_back) */ null
                    )
                }
            }
        },
        actions = {
            if (navigation.onHistoryRequested != null) {
                IconButton(
                    onClick = navigation.onHistoryRequested,
                    modifier = Modifier.testTag(NavigateToHistoryTestTag)
                ) {
                    Icon(
                        Icons.Default.List,
                        contentDescription = /* stringResource(id = R.string.top_bar_navigate_to_history) */ null
                    )
                }
            }
            if (navigation.onInfoRequested != null) {
                IconButton(
                    onClick = navigation.onInfoRequested,
                    modifier = Modifier.testTag(NavigateToInfoTestTag)
                ) {
                    Icon(
                        Icons.Default.Info,
                        contentDescription = /* stringResource(id = R.string.top_bar_navigate_to_info) */ null
                    )
                }
            }
        }
    )}
