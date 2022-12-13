package com.example.battleships.home

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.battleships.TAG
import com.example.battleships.auth.AuthActivity
import com.example.battleships.game.GameActivity
import com.example.battleships.info.InfoActivity
import com.example.battleships.rankings.RankingsActivity
import com.example.battleships.ui.Handler
import com.example.battleships.ui.NavigationHandlers
import com.example.battleships.ui.StartScreen
import com.example.battleships.ui.StartScreenNew
import com.example.battleships.user_home.UserHomeActivity

const val NavigateToAuthenticationButtonTestTag = "NavigateToAuthenticationButton"
const val NavigateToRankingsButtonTestTag = "NavigateToRankingsButton"

class HomeActivity : ComponentActivity() {

    companion object {
        var TOKEN_EXTRA : String? = null
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
            Log.e(TAG, "user home token = $TOKEN_EXTRA")
            if(TOKEN_EXTRA == null){
                StartScreenNew(
                    tag = "HomeScreen",
                    onSignIn = { AuthActivity.navigate(this) },
                    onRanking = { RankingsActivity.navigate(this) },
                    onAppInfo = { InfoActivity.navigate(this) }
                )
            }
            else {
                StartScreen(
                    Handler("Start Game", "start-game") {
                        GameActivity.navigate(this, token)
                    },
                    tag = "UserHomeScreen",
                    onNavigationRequested = NavigationHandlers(
                        onInfoRequested = { InfoActivity.navigate(origin = this) },
                        onBackRequested = { finish() }
                    )
                )
            }
        }
    }


    private val token: String
        get() =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                intent.getParcelableExtra(TOKEN_EXTRA, String::class.java) ?: TODO("Token is null")
            else
                intent.getStringExtra(TOKEN_EXTRA) ?: TODO("Token is null")


}