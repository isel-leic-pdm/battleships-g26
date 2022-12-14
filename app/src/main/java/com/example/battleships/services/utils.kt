package com.example.battleships.services

import android.util.Log
import com.example.battleships.utils.hypermedia.SirenMediaType
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.lang.reflect.Type
import java.net.URL

sealed class RequestMethod(val url: URL)
class Get(url: URL) : RequestMethod(url)
class Post(url: URL, val body: String) : RequestMethod(url)
class Put(url: URL, val body: String) : RequestMethod(url)
class Delete(url: URL) : RequestMethod(url)

/**
 * Builds a request.
 */
internal fun buildRequest(requestMethod: RequestMethod, token: String? = null, mode: Mode): Request {
    val headers =
        if (token != null) Headers.Builder().add("Authorization", "Bearer $token")
        else Headers.Builder()

    return with(Request.Builder()) {
        when (mode) {
            Mode.FORCE_REMOTE -> cacheControl(CacheControl.FORCE_NETWORK)
            Mode.FORCE_LOCAL -> cacheControl(CacheControl.FORCE_CACHE)
            else -> this
        }
    }.url(requestMethod.url).method(
        method = when (requestMethod) {
            is Get -> "GET"
            is Post -> "POST"
            is Put -> "PUT"
            is Delete -> "DELETE"
        }, body = when (requestMethod) {
            is Post -> requestMethod.body.toRequestBody()
            is Put -> requestMethod.body.toRequestBody()
            else -> null
        }
    ).headers(
        when (requestMethod) {
            is Post -> headers.add("Content-Type", "application/json").build()
            is Put -> headers.add("Content-Type", "application/json").build()
            else -> headers.build()
        }
    ).build()
}

val JsonMediaType = "application/json".toMediaType()

/**
 * This method's usefulness is circumstantial. In more realistic scenarios
 * we will not be handling API responses with this simplistic approach.
 */
internal fun <T> handleResponse(jsonEncoder: Gson, response: Response, type: Type, mediaType: MediaType): T {
    val contentType = response.body?.contentType()
    return if (response.isSuccessful && contentType != null && contentType == mediaType) {
        try {
            val body = response.body?.string()
            jsonEncoder.fromJson<T>(body, type)
        } catch (e: JsonSyntaxException) {
            val body = response.body?.string()
            Log.e("handleResponse", "Error parsing response", e)
            throw UnexpectedResponseException(response, body)
        }
    }
    else {
        val body = response.body?.string()
        throw UnexpectedResponseException(response = response, body)
    }
}