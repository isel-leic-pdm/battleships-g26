package com.example.battleships.rankings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.battleships.ui.*
import com.example.battleships.ui.theme.*
import com.example.battleships.utils.SCREEN_HEIGHT
import com.example.battleships.utils.SCREEN_WIDTH
import pt.isel.battleships.R

// Test tags for the Rankings screen
const val RankingListTestTag = "RankingList"
const val RefreshButtonTestTag = "RefreshButton"

@Composable
fun RankingsScreen(
    rankings: UserRanking?,
    refreshingState: RefreshingState,
    onRefresh: () -> Unit,
    onNavigationRequested: NavigationHandlers = NavigationHandlers(),
    onUserClick: (Int) -> Unit,
) {
    RankingList(
        rankings,
        refreshingState,
        onNavigationRequested,
        onRefresh,
        onUserClick
    )
}

@Composable
private fun RankingList(
    ranking: UserRanking?,
    refreshingState: RefreshingState,
    onNavigationRequested: NavigationHandlers,
    onRefreshRequested: () -> Unit,
    onUserClick : (Int) -> Unit,
) {
    val auxRanking = remember {
        mutableStateOf(ranking)
    }

    auxRanking.value = ranking

    BattleshipsTheme {
        Scaffold(
            modifier = Modifier.testTag("RankingsScreen"),
            topBar = {
                CenteredTopAppBar(
                    navigation = onNavigationRequested,
                    title = stringResource(R.string.ranking_top_app_bar_tittle_text)
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
            Column (
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                val search = remember {
                    mutableStateOf("")
                }

                Row(
                    Modifier
                        .offset(y = (-20).dp)
                        .width(SCREEN_WIDTH.dp / 3)
                        .clip(RoundedCornerShape(13.dp))
                        .background(Milk)
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ){
                    TextField(
                        value = search.value,
                        onValueChange = { search.value = it },
                        label = { Text("Search") },
                        modifier = Modifier
                            .background(Color.Transparent)
                            .width(250.dp)
                    )
                    IconButton(onClick = { auxRanking.value = UserRanking(
                        auxRanking.value?.users?.filter { it.username.contains(search.value) } ?: emptyList()
                    )}) {
                        Icon(
                            Icons.Default.Search,
                            null,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(onClick = { auxRanking.value = ranking; search.value = ""}) {
                        Icon(
                            Icons.Default.Clear,
                            null,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                }
                Row(Modifier.height(10.dp)) {}
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
                        Modifier
                            .fillMaxWidth()
                            .height(40.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        val fontSize = 20.sp
                        Text(stringResource(R.string.rankings_screen_table_col_1_title), fontSize = fontSize)
                        Text(stringResource(R.string.rankings_screen_table_col_2_title), fontSize = fontSize)
                        Text(stringResource(R.string.rankings_screen_table_col_3_title), fontSize = fontSize)
                        Text(stringResource(R.string.rankings_screen_table_col_4_title), fontSize = fontSize)
                    }
                    Column(
                        Modifier.verticalScroll(rememberScrollState())
                    ){
                        Row(Modifier.height(20.dp)) {}
                        auxRanking.value?.users?.forEachIndexed { idx, it ->
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
    Row(
        Modifier
            .fillMaxWidth()
            .height(10.dp)){}

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
    val fakeRankings = UserRanking(
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
