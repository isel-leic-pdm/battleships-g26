package com.example.battleships.rankings

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.material.BottomAppBar
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.battleships.ui.NavigationHandlers
import com.example.battleships.ui.TopBar
import com.example.battleships.ui.theme.BattleshipsTheme
import pt.isel.battleships.R

val NavigateToAuthenticationButtonTestTag = "NavigateToAuthenticationButton"
val NavigateToRankingsButtonTestTag = "NavigateToRankingsButton"

@Composable
fun RankingsScreen(
    onNavigationRequested: NavigationHandlers = NavigationHandlers()
) {
    BattleshipsTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            backgroundColor = Color.Black,
            topBar = { TopBar(navigation = onNavigationRequested) },
            bottomBar = { BottomAppBar {} },
        ) { innerPadding ->
            // Img()
            LazyColumn(Modifier.padding(innerPadding)) {
                item {

                }
            }
        }
    }
}

@Composable
private fun LazyItemScope.Item() {

}

@Preview
@Composable
fun StartScreenPreview() {

}