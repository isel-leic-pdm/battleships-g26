package com.example.battleships.start

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.fleetbattletemp.start.info.InfoActivity

class StartActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            StartScreen {
                navigateToInfoScreen()
            }
        }
    }

    private fun navigateToInfoScreen() {
        val intent = Intent(this, InfoActivity::class.java)
        startActivity(intent)
    }

}