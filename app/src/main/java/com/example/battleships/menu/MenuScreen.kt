package com.example.battleships.menu

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.battleships.menu.views.LoadingState
import com.example.battleships.home.Menu
import com.example.battleships.ui.TopBar
import com.example.battleships.ui.theme.BattleshipsTheme

@Composable
internal fun MenuScreen(
    onBackRequested: () -> Unit = { },
    isCreated: LoadingState,
    isLogin: LoadingState,
    onCreateUser: (String, String) -> Unit,
    onLoginUser: (String, String) -> Unit,
) {
    BattleshipsTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            backgroundColor = MaterialTheme.colors.background,
            topBar = { TopBar(onBackRequested = { onBackRequested() }) }
        ) { padding ->
            Column(
                verticalArrangement = Arrangement.SpaceAround,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
            ) {
                Menu(
                    isCreated,
                    isLogin,
                    onCreateUser,
                    onLoginUser,
                )
            }
        }
    }
}