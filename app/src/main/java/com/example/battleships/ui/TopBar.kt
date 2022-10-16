package com.example.battleships.ui

import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.battleships.R

@Composable
fun TopBar(
    onBackRequested: (() -> Unit)? = null,
    onInfoRequested: (() -> Unit)? = null,
) {
    TopAppBar(
        title = { Text(stringResource(id = R.string.app_name)) },
        navigationIcon = {
            if (onBackRequested != null) {
                IconButton(onClick = onBackRequested) {
                    Icon(Icons.Default.ArrowBack, contentDescription = null)
                }
            }
        },
        actions = {
            if (onInfoRequested != null) {
                IconButton(onClick = { onInfoRequested() }) {
                    Icon(Icons.Default.Info, contentDescription = null)
                }
            }
        }
    )
}