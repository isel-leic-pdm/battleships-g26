package com.example.battleships.services

import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.net.URI

data class Problem(
    val type: URI,
    val title: String,
    val detail: String,
) {
    companion object {
        fun getType(): TypeToken<Problem> =
            object : TypeToken<Problem>() {}
    }
}

val problemType: Type = Problem.getType().type