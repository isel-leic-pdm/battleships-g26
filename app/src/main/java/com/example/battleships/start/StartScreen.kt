package com.example.battleships.start

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.BottomAppBar
import androidx.compose.material.Button
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.battleships.ui.TopBar
import com.example.battleships.ui.theme.BattleshipsTheme
import pt.isel.battleships.R


@Composable
fun StartScreen(
    onStartGame: () -> Unit,
    onInfoRequest: () -> Unit
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
            Img()
            Column(
                verticalArrangement = Arrangement.SpaceAround,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
            ) {
                Column(
                    verticalArrangement = Arrangement.Bottom,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                ) {
                    Button(
                        onClick = onStartGame,
                        modifier = Modifier.height(60.dp)
                    ) {
                        Text(text = "Start Game")
                    }
                }
            }
        }
    }
}

@Composable
fun Img() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .offset(0.dp, (-100).dp),
    ) {
        Image(
            painterResource(id = R.drawable.ship_w),
            contentDescription = "",
            contentScale = ContentScale.Fit,
            modifier = Modifier.matchParentSize(),
        )
    }

}

@Preview
@Composable
fun StartScreenPreview() {
    StartScreen({}, {})
}