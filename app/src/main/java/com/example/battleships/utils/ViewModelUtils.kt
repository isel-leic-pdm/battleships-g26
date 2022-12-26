package com.example.battleships.utils

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.battleships.services.ApiException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


fun <T> Result<T>.getWith(context : Context): T? {
    return try {
        this.getOrThrow()
    }catch (e : ApiException) {
        Toast.makeText(context, e.toast, Toast.LENGTH_SHORT).show()
        null
    }
}


val ApiErrorHandler = fun(context: Context) : (Exception) -> Unit {
    return {
        if(it is ApiException)
            Toast.makeText(context, it.toast, Toast.LENGTH_SHORT).show()
        else Toast.makeText(context, "Internal Error", Toast.LENGTH_SHORT).show()
    }
}


fun CoroutineScope.launchWithErrorHandling(
    exHandling : (Exception) -> Unit,
    f : suspend () -> Unit,
) {
    this.launch{
        try {
            f()
        }catch (e : Exception) {
            Log.e("Exception", e.message ?: "Error occurred")
            exHandling(e)
        }
    }
}

