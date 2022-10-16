package com.example.battleships.menu

import android.content.Intent
import androidx.activity.ComponentActivity
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
import com.example.battleships.ui.TopBar
import com.example.battleships.game.GameActivity
import com.example.battleships.menu.views.Menu
import com.example.fleetbattletemp.ui.theme.FleetBattleTempTheme

@Composable
internal fun MenuScreen(
    onBackRequested: () -> Unit = { },
    isCreated: LoadingState,
    isLogin: LoadingState,
    onCreateUser: (String, String) -> Unit,
    onLoginUser: (String, String) -> Unit,
) {
    FleetBattleTempTheme {
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

internal fun navigateToGameScreen(activity: ComponentActivity) {
    val intent = Intent(activity, GameActivity::class.java)
    activity.startActivity(intent)
}