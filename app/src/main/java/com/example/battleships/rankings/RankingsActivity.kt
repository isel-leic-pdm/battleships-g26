package com.example.battleships.rankings

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.battleships.game.GameActivity
import com.example.battleships.info.InfoActivity

class RankingsActivity : ComponentActivity() {

    companion object {
        private const val TOKEN_EXTRA = "START_GAME_TOKEN_EXTRA"
        fun navigate(origin: Activity, token: String) {
            with(origin) {
                val intent = Intent(this, RankingsActivity::class.java)
                intent.putExtra(TOKEN_EXTRA, token)
                startActivity(intent)
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    val vm by viewModels<RankingsViewModel> {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return RankingsViewModel(token) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StartScreen(
                onStartGame = { GameActivity.navigate(this, vm.token) },
                onInfoRequest = { InfoActivity.navigate(this) }
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