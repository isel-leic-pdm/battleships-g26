package com.example.battleships.ui

import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.text.style.TextAlign

@Composable
fun CenteredTopAppBar(title: String, navigation: NavigationHandlers = NavigationHandlers()) {
    TopAppBar(
        title = {
            Text(
                title,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        },
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
                        contentDescription = null
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
                        contentDescription = null
                    )
                }
            }
        }
    )
}

