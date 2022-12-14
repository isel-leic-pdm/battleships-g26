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
import com.example.battleships.ui.StartScreenNew

const val NavigateToAuthenticationButtonTestTag = "NavigateToAuthenticationButton"
const val NavigateToRankingsButtonTestTag = "NavigateToRankingsButton"

class HomeActivity : ComponentActivity() {

    companion object {
        val HOME_TOKEN_EXTRA : String? = null
        fun navigate(origin: Activity, token: String) {
            with(origin) {
                val intent = Intent(this, HomeActivity::class.java)
                Log.e(TAG, "token = $token")
                intent.putExtra(HOME_TOKEN_EXTRA, token)
                startActivity(intent)
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Log.e(TAG, "user home token = $token")
            val handler = if(token == null){
                Handler("Sign In", "sign-in"){
                    AuthActivity.navigate(this)
                }
            }else Handler("Start Game", "start-game") {
                GameActivity.navigate(this, token!!)
            }
            StartScreenNew(
                handler,
                tag = "HomeScreen",
                onRanking = { RankingsActivity.navigate(this) },
                onAppInfo = { InfoActivity.navigate(this) }
            )
        }
    }


    private val token: String?
        get() =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                intent.getParcelableExtra(HOME_TOKEN_EXTRA, String::class.java)
            else
                intent.getStringExtra(HOME_TOKEN_EXTRA)


}