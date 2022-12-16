package com.example.battleships.services

import com.example.battleships.utils.hypermedia.APPLICATION_TYPE
import com.google.gson.reflect.TypeToken
import okhttp3.MediaType.Companion.toMediaType
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
        const val MEDIA_TYPE = "$APPLICATION_TYPE/problem+json"
    }
}

val problemType: Type = Problem.getType().type
val ProblemMediaType = Problem.MEDIA_TYPE.toMediaType()