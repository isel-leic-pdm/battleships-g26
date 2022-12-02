package com.example.battleships.info

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.battleships.ui.NavigationHandlers

class InfoActivity : ComponentActivity() {

    companion object {
        fun navigate(origin: Activity) {
            with(origin) {
                val intent = Intent(this, InfoActivity::class.java)
                startActivity(intent)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            InfoScreen(
                navigationHandlers = NavigationHandlers(
                    onBackRequested = { finish() }
                )
            )
        }
    }
}