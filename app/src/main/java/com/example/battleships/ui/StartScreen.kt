package com.example.battleships.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.battleships.home.NavigateToRankingsButtonTestTag
import com.example.battleships.ui.theme.BattleshipsTheme
import com.example.battleships.utils.SCREEN_HEIGHT
import pt.isel.battleships.R

data class Handler(val name: String, val tag: String, val handler: () -> Unit)

@Composable
private fun NavigationButton(title: String, tagName: String, onClick: () -> Unit) {
    Button(
        modifier = Modifier.testTag(tagName),
        onClick = onClick,
    ) {
        Text(text = title)
    }
}

@Composable
fun StartScreen(
    vararg handlers: Handler,
    tag: String? = null,
    onRanking : () -> Unit = {},
    onAppInfo : () -> Unit = {},
    onUserInfo : (() -> Unit)? = null,
) {
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
                Image(
                    painter = painterResource(id = R.drawable.ship_logo),
                    contentDescription = null,
                    modifier = Modifier.size((SCREEN_HEIGHT/12).dp)
                )
                handlers.forEach { handler ->
                    NavigationButton(title = handler.name, tagName = handler.tag) { handler.handler() }
                }
            }
        }
    }
}

@Composable
fun BottomAppNavigation(ranking : () -> Unit, appInfo : () -> Unit, onUserInfo : (() -> Unit)?)  = Row(
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
                Icons.Default.List,
                null
            )
        }
    }

    if(onUserInfo != null) {
        Column(
            horizontalAlignment = Alignment.End
        ) {
            IconButton(
                modifier = Modifier.testTag(NavigateToInfoTestTag),
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
            modifier = Modifier.testTag(NavigateToInfoTestTag),
            onClick = appInfo,
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