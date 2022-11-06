package com.example.battleships.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.BottomAppBar
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import com.example.battleships.ui.TopBar
import com.example.battleships.ui.theme.BattleshipsTheme

val NavigateToAuthenticationButtonTestTag = "NavigateToAuthenticationButton"
val NavigateToRankingsButtonTestTag = "NavigateToRankingsButton"

@Composable
fun StartScreen(
    onAuthenticateRequested: () -> Unit,
    onRankingsRequest: () -> Unit,
) {
    BattleshipsTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            backgroundColor = Color.Black,
            topBar = {
                TopBar(
                    onInfoRequested = onInfoRequest
                )
            },
            bottomBar = { BottomAppBar {} },
        ) { innerPadding ->
            // Img()
            Column(
                verticalArrangement = Arrangement.SpaceAround,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
            ) {
                NavigationButton(title = "Authenticate", NavigateToAuthenticationButtonTestTag) { onAuthenticateRequested() }
                NavigationButton(title = "Rankings", NavigateToRankingsButtonTestTag) { onRankingsRequest() }
                NavigationButton(title = "Info", NavigateToInfoButtonTestTag) { onInfoRequest() }
            }
        }
    }
}

@Composable
private fun NavigationButton(title: String, tagName: String, onClick: () -> Unit) {
    Button(
        modifier = Modifier.testTag(tagName),
        onClick = onClick,
    ) {
        Text(text = title)
    }
}

@Preview
@Composable
fun StartScreenPreview() {
    StartScreen({}, {}, {})
}