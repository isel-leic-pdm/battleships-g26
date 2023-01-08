package com.example.battleships.auth.views

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.battleships.TAG
import com.example.battleships.ui.typography

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
            .fillMaxWidth(0.4f),
        shape = RoundedCornerShape(15.dp)
    ) {
        Text(
            text = if (state === LoadingState.Loading) "Loading..." else value,
            fontSize = 16.sp,
            style = typography.h1
        )
    }
}
