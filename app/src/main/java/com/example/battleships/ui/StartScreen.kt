package com.example.battleships.ui

import android.util.DisplayMetrics
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.List
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.battleships.ui.theme.BattleshipsTheme

data class Handler(val name: String, val tag: String, val handler: () -> Unit)
const val REGISTER_BUTTON_TAG = "REGBUTTON"

@Composable
fun StartScreen(
    vararg handlers: Handler,
    tag: String? = null,
    onNavigationRequested: NavigationHandlers = NavigationHandlers(),
) {
    BattleshipsTheme {
        val modifier = if (tag != null) Modifier.testTag(tag) else Modifier
        Scaffold(
            modifier = modifier.fillMaxSize(),
            topBar = {
                        TopBar(navigation = onNavigationRequested)
                     },
            bottomBar = { BottomAppBar {} },
        ) { innerPadding ->
            Column(
                verticalArrangement = Arrangement.SpaceAround,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
            ) {
                handlers.forEach { handler ->
                    NavigationButton(title = handler.name, tagName = handler.tag) { handler.handler() }
                }
            }
        }
    }
}

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
fun StartScreenNew(
    onSignIn : () -> Unit = {},
    onRanking : () -> Unit = {},
    onAppInfo : () -> Unit = {},
    onNavigationRequested: NavigationHandlers = NavigationHandlers(),
) {
    BattleshipsTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopBar(navigation = onNavigationRequested)
            },
            bottomBar = { BottomAppBar(
                content = {
                    BottomAppNavigation(onRanking,onAppInfo)
                }
            ) },
        ) { innerPadding ->
            Column(
                verticalArrangement = Arrangement.SpaceAround,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
            ) {
                Button(
                    onClick = onSignIn
                ) {
                    Text("Sign In")
                }
            }
        }
    }
}

@Composable
fun BottomAppNavigation(ranking : () -> Unit, appInfo : () -> Unit)  = Row(
    horizontalArrangement = Arrangement.Center,
    verticalAlignment = Alignment.CenterVertically
) {
    Column {
        IconButton(
            onClick = ranking,
        ) {
            Icon(
                Icons.Default.List,
                null
            )
        }
    }
    Column(
        Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.End
    ) {
        IconButton(
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
    StartScreenNew()
}