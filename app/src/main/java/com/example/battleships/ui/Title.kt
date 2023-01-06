package com.example.battleships.ui

import android.icu.text.CaseMap.Title
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pt.isel.battleships.R

enum class TitleSize { H1, H2, H3, H4, H5, H6 }

@Composable
fun Title(text: String, size: TitleSize) {
    val style = when (size) {
        TitleSize.H1 -> MaterialTheme.typography.h1
        TitleSize.H2 -> MaterialTheme.typography.h2
        TitleSize.H3 -> MaterialTheme.typography.h3
        TitleSize.H4 -> MaterialTheme.typography.h4
        TitleSize.H5 -> MaterialTheme.typography.h5
        TitleSize.H6 -> MaterialTheme.typography.h6
    }
    Text(
        text.uppercase(),
        modifier = Modifier
            .padding(15.dp),
        style = style,
        color = MaterialTheme.colors.primary,
        fontWeight = FontWeight.ExtraBold,
        textAlign = TextAlign.Center,
        fontFamily = style.fontFamily,
        letterSpacing = 1.sp
    )
}