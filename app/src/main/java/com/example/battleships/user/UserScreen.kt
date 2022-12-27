package com.example.battleships.user

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.battleships.rankings.UserStats
import com.example.battleships.ui.CenteredTopAppBar
import com.example.battleships.ui.NavigationHandlers
import com.example.battleships.ui.theme.BattleshipsTheme
import com.example.battleships.ui.theme.Milk
import pt.isel.battleships.R

@Composable
fun UserScreen(user: UserStats, onBackRequest: () -> Unit) {
    BattleshipsTheme {
        Scaffold(
            topBar = { CenteredTopAppBar(title = "User", NavigationHandlers(onBackRequest)) }
        ) { padding ->
            Column(
                Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .offset(y = 60.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top

            ) {
                Image(
                    painter = painterResource(id = R.drawable.profile_user),
                    contentDescription = null,
                    modifier = Modifier
                        .size(150.dp)
                        .padding(10.dp)
                )

                Text(
                    text = user.username,
                    fontSize = 30.sp,
                    modifier = Modifier.padding(20.dp)
                )

                Row(
                    Modifier
                        .fillMaxWidth().offset(y = 15.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Column(
                        Modifier
                            .width(150.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Milk)
                            .padding(10.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Wins",
                            fontSize = 20.sp
                        )
                        Text(
                            text = user.wins.toString(),
                            fontSize = 20.sp
                        )
                    }

                    Spacer(modifier = Modifier.size(30.dp))

                    Column(
                        Modifier
                            .width(150.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Milk)
                            .padding(10.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Games played",
                            fontSize = 20.sp
                        )
                        Text(
                            text = user.gamesPlayed.toString(),
                            fontSize = 20.sp
                        )
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun previewUser() {
    UserScreen(user = UserStats(0, "Antonio Carvalho", 10, 10)) {
    }
}