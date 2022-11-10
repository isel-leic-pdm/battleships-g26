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
import com.example.battleships.ui.NavigationHandlers
import com.example.battleships.ui.StartScreen

class RankingsActivity : ComponentActivity() {

    companion object {
        fun navigate(origin: Activity) {
            with(origin) {
                val intent = Intent(this, RankingsActivity::class.java)
                startActivity(intent)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RankingsScreen(
                NavigationHandlers(
                    onInfoRequested = { InfoActivity.navigate(origin = this) },
                )
            )
        }
    }
}