package com.example.battleships.home

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.battleships.auth.AuthActivity
import com.example.battleships.info.InfoActivity
import com.example.battleships.rankings.RankingsActivity
import com.example.battleships.ui.Handler
import com.example.battleships.ui.NavigationHandlers
import com.example.battleships.ui.StartScreen
import com.example.battleships.ui.StartScreenNew

const val NavigateToAuthenticationButtonTestTag = "NavigateToAuthenticationButton"
const val NavigateToRankingsButtonTestTag = "NavigateToRankingsButton"

class HomeActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StartScreenNew(
                onSignIn = { AuthActivity.navigate(this) },
                onRanking = { RankingsActivity.navigate(this) },
                onAppInfo = { InfoActivity.navigate(this) }
            )
        }
    }
}