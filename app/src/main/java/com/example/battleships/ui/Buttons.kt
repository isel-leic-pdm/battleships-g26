package com.example.battleships.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Button1(text: String, testTag: String, onClick: () -> Unit) {
    val modifier = Modifier
        .fillMaxWidth(0.7f)
        .height(50.dp)
    ButtonInternal(modifier, text, testTag, onClick)
}

@Composable
fun Button2(text: String, testTag: String, onClick: () -> Unit) {
    val modifier = Modifier
        .fillMaxWidth(0.5f)
        .height(70.dp)
    ButtonInternal(modifier, text, testTag, onClick)
}

@Composable
private fun ButtonInternal(modifier: Modifier, text: String, testTag: String, onClick: () -> Unit) {
    Button(
        modifier = modifier.testTag(testTag),
        onClick = onClick
    ) {
        Text(
            text = text,
            fontSize = 20.sp
        )
    }
}