package com.example.battleships.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomAppBar
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import com.example.battleships.ui.theme.BattleshipsTheme

val NavigateToAuthenticationButtonTestTag = "NavigateToAuthenticationButton"
val NavigateToRankingsButtonTestTag = "NavigateToRankingsButton"

data class Handler(val name: String, val tag: String, val handler: () -> Unit)

@Composable
fun StartScreen(
    vararg handlers: Handler,
    onNavigationRequested: NavigationHandlers = NavigationHandlers(),
) {
    BattleshipsTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            backgroundColor = Color.Black,
            topBar = { TopBar(navigation = onNavigationRequested) },
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
                handlers.forEach { handler ->
                    NavigationButton(title = handler.name, tagName = handler.tag) { handler.handler() }
                }
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