package com.example.battleships.home

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.battleships.home.NavigateToRankingsButtonTestTag
import com.example.battleships.ui.Button1
import com.example.battleships.ui.NavigateToAppInfoTestTag
import com.example.battleships.ui.theme.BattleshipsTheme
import com.example.battleships.ui.typography
import com.example.battleships.utils.SCREEN_HEIGHT
import pt.isel.battleships.R

const val NavigateToUserInfoButton = "NavigateToUserInfoButton"

data class Handler(val name: String, val tag: String, val handler: () -> Unit)

const val TAG = "StartScreen"

@Composable
fun StartScreen(
    vararg handlers: Handler,
    tag: String? = null,
    onRanking : () -> Unit = {},
    onAppInfo : () -> Unit = {},
    onUserInfo : (() -> Unit)? = null,
) {
    Log.d(TAG, "Composing StartScreen")
    BattleshipsTheme {
        val modifier = if (tag != null) Modifier.testTag(tag) else Modifier
        Scaffold(
            modifier = modifier.fillMaxSize(),
            bottomBar = { BottomAppBar(
                content = {
                    BottomAppNavigation(onRanking, onAppInfo, onUserInfo)
                }
            ) },
        ) { innerPadding ->
            Column(
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Icon(
                        Icons.Default.DirectionsBoat,
                        null,
                        modifier = Modifier.size((SCREEN_HEIGHT/12).dp).padding(15.dp)
                    )
                    Text(
                        text = stringResource(id = R.string.app_name),
                        fontSize = 35.sp, fontFamily = FontFamily.SansSerif
                    )
                }
                handlers.forEach { handler ->
                    Button1(text = handler.name, testTag = handler.tag) { handler.handler() }
                }
            }
        }
    }
}

@Composable
fun BottomAppNavigation(ranking : () -> Unit, onAppInfo : () -> Unit, onUserInfo : (() -> Unit)?)  = Row(
    Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
) {
    Column {
        IconButton(
            modifier = Modifier.testTag(NavigateToRankingsButtonTestTag),
            onClick = ranking,
        ) {
            Icon(
                Icons.Default.Leaderboard,
                null
            )
        }
    }

    if(onUserInfo != null) {
        Column(
            horizontalAlignment = Alignment.End
        ) {
            IconButton(
                modifier = Modifier.testTag(NavigateToUserInfoButton),
                onClick = onUserInfo,
            ) {
                Icon(
                    Icons.Default.AccountCircle,
                    null
                )
            }
        }
    }

    Column(
        horizontalAlignment = Alignment.End
    ) {
        IconButton(
            modifier = Modifier.testTag(NavigateToAppInfoTestTag),
            onClick = onAppInfo,
        ) {
            Icon(
                Icons.Default.Info,
                null
            )
        }
    }
}

@Preview
@Composable
fun NewStartScreenPreview(){
    StartScreen()
}