package com.example.battleships.auth.views

import android.util.Log
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import com.example.battleships.TAG
import com.example.battleships.home.CreateUserButtonTestTag

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
