package com.example.battleships.rankings

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.BottomAppBar
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import com.example.battleships.ui.NavigationHandlers
import com.example.battleships.ui.TopBar
import com.example.battleships.ui.theme.BattleshipsTheme

// Test tags for the Rankings screen
const val RankingListTestTag = "RankingList"
const val RefreshButtonTestTag = "RefreshButton"

@Composable
fun RankingsScreen(
    ranking: GameRanking,
    onRefreshRequested: () -> Unit,
    onNavigationRequested: NavigationHandlers = NavigationHandlers()
) {
    BattleshipsTheme {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .testTag("RankingsScreen"),
            backgroundColor = Color.White,
            topBar = { TopBar(navigation = onNavigationRequested) },
            bottomBar = { BottomAppBar {} },
        ) { innerPadding ->
            LazyColumn(
                Modifier
                    .padding(innerPadding)
                    .fillMaxWidth()
                    .testTag(RankingListTestTag)) {
                ranking.users.forEach { user ->
                    item {
                        UserItem(user)
                    }
                }
                item {
                    Button(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag(RefreshButtonTestTag),
                        onClick = onRefreshRequested
                    ) { Text("Refresh") }
                }
            }
        }
    }
}

@Composable
private fun UserItem(user: UserStats) {
    Row(Modifier.fillMaxWidth()) {
        Text(text = "Username: ${user.username}")
        Text(text = "Wins: ${user.wins}")
        Text(text = "Games played: ${user.gamesPlayed}")
    }
}

@Preview
@Composable
fun StartScreenPreview() {

}