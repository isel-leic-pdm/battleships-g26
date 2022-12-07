package com.example.battleships

import androidx.compose.runtime.Composable
import com.example.battleships.services.ApiException
import com.example.battleships.utils.ErrorAlert
import pt.isel.battleships.R
import java.io.IOException

@Composable
fun ErrorMessage(onNonError: () -> Unit, onIoExceptionDismiss: () -> Unit, onApiExceptionDismiss: () -> Unit) {
    try { onNonError() }
    catch (e: IOException) {
        ErrorAlert(
            title = R.string.error_api_title,
            message = R.string.error_could_not_reach_api,
            buttonText = R.string.error_retry_button_text,
            onDismiss = { onIoExceptionDismiss() }
        )
    }
    catch (e: ApiException) {
        ErrorAlert(
            title = R.string.error_api_title,
            message = R.string.error_unknown_api_response,
            buttonText = R.string.error_exit_button_text,
            onDismiss = { onApiExceptionDismiss() }
        )
    }
}