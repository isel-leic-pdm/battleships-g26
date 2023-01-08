package com.example.battleships.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun Button1(text: String, testTag: String,  color : Color = MaterialTheme.colors.primary, onClick: () -> Unit) {
    val modifier = Modifier
        .fillMaxWidth(0.7f)
        .padding(10.dp)
    ButtonInternal(modifier, text, testTag, onClick, color)
}

@Composable
internal fun ButtonInternal(modifier: Modifier, text: String, testTag: String, onClick: () -> Unit, color : Color) {
    val colors = ButtonDefaults.buttonColors(
        backgroundColor = color,
        contentColor = MaterialTheme.colors.background
    )

    Button(
        modifier = modifier.testTag(testTag),
        onClick = onClick,
        shape = RoundedCornerShape(17.dp),
        colors = colors
    ) {
        Text(
            text = text,
            fontSize = 18.sp,
            style = typography.h1,
        )
    }
}