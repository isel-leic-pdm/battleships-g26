package com.example.battleships.menu.views

import android.util.Log
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.example.battleships.TAG

enum class LoadingState { Idle, Loading }

@Composable
fun LoadingButton(
    value: String,
    state: LoadingState,
    onClick: () -> Unit
) {
    Log.v(TAG, "LoadingButton composed")
    Button(
        enabled = state == LoadingState.Idle,
        onClick = onClick,
    ) {
        Text(text = if (state === LoadingState.Loading) "Loading..." else value)
    }
}