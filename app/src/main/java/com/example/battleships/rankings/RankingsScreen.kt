package com.example.battleships.rankings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.FabPosition
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.battleships.ui.*
import com.example.battleships.ui.theme.*
import com.example.battleships.utils.SCREEN_HEIGHT
import com.example.battleships.utils.SCREEN_WIDTH

// Test tags for the Rankings screen
const val RankingListTestTag = "RankingList"
const val RefreshButtonTestTag = "RefreshButton"

@Composable
fun RankingsScreen(
    rankings: GameRanking?,
    refreshingState: RefreshingState,
    onRefresh: () -> Unit,
    onNavigationRequested: NavigationHandlers = NavigationHandlers(),
    onUserClick: (Int) -> Unit,
) {
    RankingList(
        rankings ?: GameRanking(emptyList()),
        refreshingState,
        onNavigationRequested,
        onRefresh,
        onUserClick
    )
}

@Composable
private fun RankingList(
    ranking: GameRanking,
    refreshingState: RefreshingState,
    onNavigationRequested: NavigationHandlers,
    onRefreshRequested: () -> Unit,
    onUserClick : (Int) -> Unit,
) {
    BattleshipsTheme {
        Scaffold(
            topBar = {
                CenteredTopAppBar(
                    navigation = onNavigationRequested,
                    title = "Rankings"
                )
            },
            backgroundColor = MaterialTheme.colors.background,
            floatingActionButton = {
                RefreshButton(
                    onClick = onRefreshRequested,
                    state = refreshingState
                )
            },
            floatingActionButtonPosition = FabPosition.Center,
        ) { padding ->
            Column (modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally){
                Column(
                    Modifier
                        .offset(y = (-20).dp)
                        .height(SCREEN_HEIGHT.dp / 3 - 150.dp)
                        .width(SCREEN_WIDTH.dp / 3)
                        .clip(RoundedCornerShape(30.dp))
                        .background(Milk)
                        .padding(30.dp)
                        .testTag(RankingListTestTag),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        Modifier.fillMaxWidth().height(40.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        val fontSize = 20.sp
                        Text("Nº", fontSize = fontSize)
                        Text("NAME", fontSize = fontSize)
                        Text("GAMES", fontSize = fontSize)
                        Text("WINS", fontSize = fontSize)
                    }
                    Column(
                        Modifier.verticalScroll(rememberScrollState())
                    ){
                        Row(Modifier.height(20.dp)) {}
                        ranking.users.forEachIndexed { idx, it ->
                            ListEntry(
                                id = it.id,
                                name = it.username,
                                games = it.gamesPlayed,
                                wins = it.wins, idx = idx + 1,
                                onUserClick
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun ListEntry(id : Int, name: String, games: Int, wins: Int, idx : Int, onUserClick: (Int) -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(15.dp))
            .background(MaterialTheme.colors.background)
            .padding(8.dp)
            .clickable { onUserClick(id) },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Meddle(idx = idx)
        Spacer(modifier = Modifier.size(13.dp))
        Entry(label = name)
        Entry(label = games.toString())
        Entry(label = wins.toString())
    }
    Row(Modifier.fillMaxWidth().height(10.dp)){}

}

@Composable
fun Entry(label: String) {
    Column(
        Modifier
            .width(90.dp)
            .height(60.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (label.length > 6) MarqueeText(text = label, fontSize = 20.sp)
        else Text(label, fontSize = 20.sp)
    }
}


@Composable
fun Meddle(idx : Int){
    val color = when(idx){
        1 -> Gold
        2 -> Silver
        3 -> Bronze
        else -> Color.White
    }
    Column(
        Modifier
            .size(25.dp)
            .clip(CircleShape)
            .background(color),
        verticalArrangement = Arrangement.SpaceEvenly,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(idx.toString(), fontSize = 20.sp,
            modifier = Modifier.offset(y = (-3).dp)
        )
    }
}

@Preview
@Composable
fun StartScreenPreview() {
    val fakeRankings = GameRanking(
        listOf(
            UserStats(1, "Antonio Carvalho", 5, 8),
            UserStats(1, "Miguel", 0, 0),
            UserStats(1, "Pedro", 0, 0),
            UserStats(1, "Antonio Carvalho", 0, 0),
            UserStats(1, "Miguel", 0, 0),
            UserStats(1, "Pedro", 0, 0),
            UserStats(1, "Antonio Carvalho", 0, 0),
            UserStats(1, "Miguel", 0, 0),
        )
    )
    RankingsScreen(
        fakeRankings,
        RefreshingState.Idle,
        onRefresh = {},
        onNavigationRequested = NavigationHandlers(),
        onUserClick = {}
    )
}
