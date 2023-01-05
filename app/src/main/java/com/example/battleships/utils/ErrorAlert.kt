package com.example.battleships.utils

import androidx.annotation.StringRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material.AlertDialog
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.battleships.ui.theme.BattleshipsTheme

@Composable
fun ErrorAlert(
    @StringRes title: Int,
    @StringRes message: Int,
    @StringRes rightButtonText: Int,
    @StringRes leftButtonText: Int? = null,
    onRightButton: () -> Unit = { },
    onLeftButton: () -> Unit = { },

) {
    val auxLeftButtonText = if(leftButtonText != null)
        stringResource(id = leftButtonText) else null

    ErrorAlertImpl(
        title = stringResource(id = title),
        message = stringResource(id = message),
        rightButtonText = stringResource(id = rightButtonText),
        leftButtonText = auxLeftButtonText,
        onRightButton = onRightButton,
        onLeftButton = onLeftButton
    )
}

@Composable
private fun ErrorAlertImpl(
    title: String,
    message: String,
    rightButtonText: String,
    leftButtonText: String? = null,
    onRightButton: () -> Unit = {},
    onLeftButton: () -> Unit = {},

) {
    AlertDialog(
        onDismissRequest = { },
        buttons = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if(leftButtonText != null)
                    OutlinedButton(
                        border = BorderStroke(0.dp, Color.Unspecified),
                        onClick = onLeftButton
                    ) {
                        Text(text = leftButtonText)
                    }
                else Box{}

                OutlinedButton(
                    border = BorderStroke(0.dp, Color.Unspecified),
                    onClick = onRightButton
                ) {
                    Text(text = rightButtonText)
                }
            }
        },
        title = { Text(text = title) },
        text = { Text(text = message) },
        modifier = Modifier.testTag("ErrorAlert")
    )
}

@Preview(showBackground = true)
@Composable
private fun ErrorAlertImplPreview() {
    BattleshipsTheme {
        ErrorAlertImpl(
            title = "Error accessing server",
            message = "Could not ...",
            rightButtonText = "OK",
        )
    }
}

