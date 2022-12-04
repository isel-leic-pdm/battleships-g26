package com.example.battleships.rankings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.battleships.ui.CenteredTopAppBar
import com.example.battleships.ui.MarqueeText
import com.example.battleships.ui.NavigationHandlers
import com.example.battleships.ui.theme.*
import com.example.battleships.utils.SCREEN_HEIGHT
import com.example.battleships.utils.SCREEN_WIDTH

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
            topBar = { CenteredTopAppBar(
                navigation = onNavigationRequested,
                title = "Rankings"
            ) }
        ) { padding ->
            Column(
                Modifier.fillMaxSize().testTag("RankingsScreen"),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Column(
                    Modifier
                        .offset(y = -20.dp)
                        .height(SCREEN_HEIGHT.dp/3 - 150.dp)
                        .width(SCREEN_WIDTH.dp/3)
                        .clip(RoundedCornerShape(30.dp))
                        .background(Milk)
                        .padding(30.dp)
                        .verticalScroll(rememberScrollState())
                        .testTag(RankingListTestTag),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row (Modifier.offset(x = 17.dp)){
                        Column { Text("NAME", fontSize = 25.sp) }
                        Spacer(Modifier.size(20.dp))
                        Column { Text("GAMES", fontSize = 25.sp) }
                        Spacer(Modifier.size(20.dp))
                        Column { Text("WINS", fontSize = 25.sp) }
                    }
                    Row(Modifier.height(20.dp)) {}
                    ranking.users.forEachIndexed { idx, it ->
                        ListEntry(name = it.username,
                            games = it.gamesPlayed,
                            wins = it.wins, idx = idx + 1
                        )
                    }
                }

                Button(
                    modifier = Modifier
                        .testTag(RefreshButtonTestTag)
                        .size(50.dp)
                        .clip(CircleShape),
                    onClick = onRefreshRequested
                ) {
                    Icon(Icons.Default.Refresh, null)
                }
            }
        }
    }
}


@Composable
fun ListEntry(name: String, games: Int, wins: Int, idx : Int) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(6.dp)
            .clip(RoundedCornerShape(15.dp))
            .background(MaterialTheme.colors.background)
            .padding(6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Meddle(idx = idx)
        Spacer(modifier = Modifier.size(20.dp))
        Entry(label = name)
        Entry(label = games.toString())
        Entry(label = wins.toString())
    }

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
            UserStats("Antonio Carvalho", 5, 8),
            UserStats("Miguel", 0, 0),
            UserStats("Pedro", 0, 0),
            UserStats("Antonio Carvalho", 0, 0),
            UserStats("Miguel", 0, 0),
            UserStats("Pedro", 0, 0),
            UserStats("Antonio Carvalho", 0, 0),
            UserStats("Miguel", 0, 0),
            UserStats("Pedro", 0, 0),
            UserStats("Antonio Carvalho", 0, 0),
            UserStats("Miguel", 0, 0),
            UserStats("Pedro", 0, 0),
        )
    )
    RankingsScreen(ranking = fakeRankings, onRefreshRequested = { /*TODO*/ })
}
