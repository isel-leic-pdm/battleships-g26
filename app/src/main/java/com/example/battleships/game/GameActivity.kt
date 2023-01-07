package com.example.battleships.game

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.battleships.DependenciesContainer

class GameActivity : ComponentActivity() {
    companion object {
        const val TOKEN_EXTRA = "GAME_TOKEN_EXTRA"
        fun navigate(origin: Activity, token: String) {
            with(origin) {
                val intent = Intent(this, GameActivity::class.java)
                intent.putExtra(TOKEN_EXTRA, token)
                startActivity(intent)
            }
        }
    }

    private val useCases by lazy {
        (application as DependenciesContainer).useCases
    }

    @Suppress("UNCHECKED_CAST")
    internal val vm by viewModels<GameViewModel> {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return GameViewModel(useCases, token) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        vm.checkIfUserIsInQueueOrInGame() // will check if user is in queue and if so, will display matchmaking screen
        setContent {
            GameScreen(this) { finish() }
        }
    }

    private val token: String
        get() =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                intent.getParcelableExtra(TOKEN_EXTRA, String::class.java) ?: TODO("Token is null")
            else
                intent.getStringExtra(TOKEN_EXTRA) ?: TODO("Token is null")
}