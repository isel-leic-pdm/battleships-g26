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
import androidx.compose.ui.res.stringResource
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
import com.example.battleships.utils.ErrorAlert
import com.example.battleships.utils.getWith
import pt.isel.battleships.R

const val NavigateToAuthenticationButtonTestTag = "NavigateToAuthenticationButton"
const val NavigateToRankingsButtonTestTag = "NavigateToRankingsButton"
const val NavigateToGameTestTag = "NavigateToGame"
const val HomeScreenTestTag = "HomeScreen"
const val UserHomeScreenTestTag = "UserHomeScreen"

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
    val vm by viewModels<HomeViewModel> {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return HomeViewModel(useCases) as T
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        var home :Home? = null
        super.onCreate(savedInstanceState)
        val tokenAux = token
        if (tokenAux != null) vm.getUserHome(tokenAux)
        else vm.getHome().also {
            home = vm.home?.getOrNull()
        }
        setContent {
            val context = LocalContext.current
            val handler = if (tokenAux == null) {
                Handler(
                    name = stringResource(R.string.sign_in_button_text),
                    tag = NavigateToAuthenticationButtonTestTag,
                ) {
                    AuthActivity.navigate(this)
                }
            } else Handler(
                name = stringResource(R.string.start_game_button_text),
                tag = NavigateToGameTestTag
            ) {
                GameActivity.navigate(this, tokenAux)
            }
            val onUserInfo = if (tokenAux == null) null
            else { { UserActivity.navigate(this, vm.userHome?.getWith(context)!!.userId) } }

            if(home == null)
                ErrorAlert(
                    title = R.string.error_api_title,
                    message = R.string.error_could_not_reach_api,
                    rightButtonText = R.string.error_retry_button_text,
                    leftButtonText = R.string.error_exit_button_text,
                    onRightButton = { vm.getHome() },
                    onLeftButton = { finish() }
                )
            else {
                StartScreen(
                    handler,
                    tag = if (tokenAux == null) HomeScreenTestTag else UserHomeScreenTestTag,
                    onRanking = { RankingsActivity.navigate(this) },
                    onAppInfo = { InfoActivity.navigate(this) },
                    onUserInfo = onUserInfo,
                )
            }
        }
    }


    private val token: String?
        get() =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                intent.getParcelableExtra(HOME_TOKEN_EXTRA, String::class.java)
            else
                intent.getStringExtra(HOME_TOKEN_EXTRA)


}