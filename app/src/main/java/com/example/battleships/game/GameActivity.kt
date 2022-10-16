package com.example.battleships.game

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.battleships.DependenciesContainer

class GameActivity : ComponentActivity() {
    private val service by lazy {
        (application as DependenciesContainer).battleshipsService
    }

    @Suppress("UNCHECKED_CAST")
    internal val vm by viewModels<GameViewModel> {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return GameViewModel(service) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            vm.startGame()
            GameScreen(this)
        }
    }
}