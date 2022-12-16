package com.example.battleships.utils

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.battleships.services.ApiException
import java.io.IOException


fun <T> Result<T>.getWith(context : Context): T? {
    return try {
        this.getOrThrow()
    }catch (e : ApiException) {
        Toast.makeText(context, e.toast, Toast.LENGTH_SHORT).show()
        null
    }
}