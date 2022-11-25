package com.example.battleships.auth.views

import android.util.Log
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.example.battleships.TAG

enum class LoadingState { Idle, Loading }

@Composable
fun LoadingButton(
    value: String,
    state: LoadingState,
    tagName: String,
    onClick: () -> Unit
) {
    Log.v(TAG, "LoadingButton composed")
    Button(
        enabled = state == LoadingState.Idle,
        onClick = onClick,
        modifier = Modifier.testTag(tagName)
    ) {
        Text(text = if (state === LoadingState.Loading) "Loading..." else value)
    }
}
