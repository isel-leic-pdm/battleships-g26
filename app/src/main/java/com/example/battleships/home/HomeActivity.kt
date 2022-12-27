package com.example.battleships.home

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.battleships.DependenciesContainer
import com.example.battleships.TAG
import com.example.battleships.auth.AuthActivity
import com.example.battleships.game.GameActivity
import com.example.battleships.info.InfoActivity
import com.example.battleships.rankings.RankingsActivity
import com.example.battleships.ui.Handler
import com.example.battleships.ui.StartScreen
import com.example.battleships.user.UserActivity
import com.example.battleships.user.UserViewModel
import com.example.battleships.utils.getWith

const val NavigateToAuthenticationButtonTestTag = "NavigateToAuthenticationButton"
const val NavigateToRankingsButtonTestTag = "NavigateToRankingsButton"

class HomeActivity : ComponentActivity() {

    companion object {
        val HOME_TOKEN_EXTRA: String? = null
        fun navigate(origin: Activity, token: String) {
            with(origin) {
                val intent = Intent(this, HomeActivity::class.java)
                Log.e(TAG, "token = $token")
                intent.putExtra(HOME_TOKEN_EXTRA, token)
                startActivity(intent)
            }
        }
    }

    private val useCases by lazy {
        (application as DependenciesContainer).useCases
    }

    @Suppress("UNCHECKED_CAST")
    val vm by viewModels<UserHomeViewModel> {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return UserHomeViewModel(useCases) as T
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (token != null) vm.getUserHome(token!!)
        setContent {
            val context = LocalContext.current
            Log.e(TAG, "user home token = $token")
            val handler = if (token == null) {
                Handler("Sign In", "sign-in") {
                    AuthActivity.navigate(this)
                }
            } else Handler("Start Game", "start-game") {
                GameActivity.navigate(this, token!!)
            }
            val onUserInfo = if (token == null) null
            else {
                { UserActivity.navigate(this, vm.me?.getWith(context)!!.userId) }
            }
            StartScreen(
                handler,
                tag = "HomeScreen",
                onRanking = { RankingsActivity.navigate(this) },
                onAppInfo = { InfoActivity.navigate(this) },
                onUserInfo = onUserInfo,
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