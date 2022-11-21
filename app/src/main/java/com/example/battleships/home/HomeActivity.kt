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

const val NavigateToAuthenticationButtonTestTag = "NavigateToAuthenticationButton"
const val NavigateToRankingsButtonTestTag = "NavigateToRankingsButton"

class HomeActivity : ComponentActivity() {

    companion object {
        private const val TOKEN_EXTRA = "START_GAME_TOKEN_EXTRA"
        fun navigate(origin: Activity, token: String) {
            with(origin) {
                val intent = Intent(this, HomeActivity::class.java)
                intent.putExtra(TOKEN_EXTRA, token)
                startActivity(intent)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StartScreen(
                Handler("Create/Login", NavigateToAuthenticationButtonTestTag) {
                    AuthActivity.navigate(this)
                },
                Handler("Rankings", NavigateToRankingsButtonTestTag) {
                    RankingsActivity.navigate(this)
                },
                tag = "HomeScreen",
                onNavigationRequested = NavigationHandlers(
                    onInfoRequested = { InfoActivity.navigate(origin = this) },
                    onBackRequested = { finish() }
                )
            )
        }
    }

    private val token: String
        get() =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                intent.getParcelableExtra(TOKEN_EXTRA, String::class.java) ?: TODO("Token is null")
            else
                intent.getStringExtra(TOKEN_EXTRA) ?: TODO("Token is null")
}