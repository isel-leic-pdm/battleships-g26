package com.example.battleships.utils

import android.content.Context
import android.widget.Toast
import com.example.battleships.services.ApiException


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
    }
}

