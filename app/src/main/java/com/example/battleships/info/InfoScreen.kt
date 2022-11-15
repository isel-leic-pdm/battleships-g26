package com.example.battleships.info

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.battleships.ui.NavigationHandlers
import com.example.battleships.ui.TopBar
import com.example.battleships.ui.theme.BattleshipsTheme

@Composable
fun InfoScreen(
    onInfoRequest: () -> Unit
) {
    BattleshipsTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            backgroundColor = MaterialTheme.colors.background,
            topBar = {
                TopBar(NavigationHandlers(onInfoRequest))
            },
        ) { innerPadding ->
            Column(
                verticalArrangement = Arrangement.SpaceAround,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
            ) {
                Author {}
            }
        }
    }
}


@Composable
fun Author(onSendEmailRequested: () -> Unit = { }) {
    Column(
        verticalArrangement = Arrangement.SpaceAround,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(text = "Made with love by:", style = MaterialTheme.typography.h6)
        Text(text = "António Carvalho", style = MaterialTheme.typography.h6)
        Text(text = "Pedro Silva", style = MaterialTheme.typography.h6)
        Text(text = "Miguel Rocha", style = MaterialTheme.typography.h6)
        Icon(imageVector = Icons.Default.Email,
            contentDescription = null,
            tint = Color.DarkGray,
            modifier = Modifier
                .clickable { onSendEmailRequested() }
                .size(50.dp)
        )
    }
}


@Preview
@Composable
fun AuthorPreview() {
    Author {}
}

@Preview
@Composable
fun InfoScreenPreview() {
    InfoScreen {}
}