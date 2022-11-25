package com.example.battleships.rankings

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.battleships.ui.CenteredTopAppBar
import com.example.battleships.ui.NavigationHandlers
import com.example.battleships.ui.TopBar
import com.example.battleships.ui.theme.BattleshipsTheme
import com.example.battleships.ui.theme.Milk

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
                    .testTag(RankingListTestTag)
            ) {
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


@Composable
fun RankingsScreenNew(
    ranking: GameRanking,
    onRefreshRequested: () -> Unit,
    onNavigationRequested: NavigationHandlers = NavigationHandlers()
) {
    BattleshipsTheme {
        Scaffold(
            topBar = { CenteredTopAppBar(title = "Rankings") }
        ) { padding ->
            Column(
                Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                Column(
                    Modifier
                        .offset(y = -20.dp)
                        .height(650.dp)
                        .width(360.dp)
                        .clip(RoundedCornerShape(30.dp))
                        .background(Milk)
                        .padding(20.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row{
                        Column { Text("NAME", fontSize = 25.sp)}
                        Spacer(Modifier.size(20.dp))
                        Column { Text("GAMES", fontSize = 25.sp)}
                        Spacer(Modifier.size(20.dp))
                        Column { Text("WINS", fontSize = 25.sp)}
                    }
                    Row(Modifier.height(20.dp)) {}
                    ranking.users.forEach {
                        ListEntry(name = it.username, games = it.gamesPlayed, wins = it.wins)
                    }
                }
            }
        }
    }
}


@Composable
fun ListEntry(name : String, games: Int, wins : Int){
    Row(
        Modifier
            .fillMaxWidth()
            .padding(6.dp)
            .clip(RoundedCornerShape(15.dp))
            .background(MaterialTheme.colors.background)
            .padding(6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ){
        Entry(value = name)
        Entry(value = games.toString())
        Entry(value = wins.toString())
    }
}

@Composable
fun Entry(value : String) {
    Column(Modifier.width(90.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(value, fontSize = 20.sp)
    }
}


@Preview
@Composable
fun StartScreenPreview() {
    val fakeRankings = GameRanking(
        listOf(
            UserStats("Antonio", 0, 0),
            UserStats("Miguel", 0, 0),
            UserStats("Pedro", 0, 0),
            UserStats("Antonio", 0, 0),
            UserStats("Miguel", 0, 0),
            UserStats("Pedro", 0, 0),
            UserStats("Antonio", 0, 0),
            UserStats("Miguel", 0, 0),
            UserStats("Pedro Manel", 0, 0),
            UserStats("Antonio", 0, 0),
            UserStats("Miguel", 0, 0),
            UserStats("Pedro", 0, 0),
            UserStats("Antonio", 0, 0),
            UserStats("Miguel", 0, 0),
            UserStats("Pedro", 0, 0),
        )
    )
    RankingsScreenNew(ranking = fakeRankings, onRefreshRequested = { /*TODO*/ })
}
