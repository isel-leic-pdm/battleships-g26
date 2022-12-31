package com.example.battleships.services

import android.util.Log
import com.example.battleships.utils.hypermedia.SirenAction
import com.example.battleships.utils.hypermedia.SirenLink
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import java.lang.reflect.Type
import java.net.URL

sealed class RequestMethod(val url: URL)
class Get(url: URL) : RequestMethod(url)
class Post(url: URL, val body: String?) : RequestMethod(url)
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
            is Post -> requestMethod.body?.toRequestBody()
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

    if(contentType != ProblemMediaType && contentType != mediaType) {
        val body = response.body?.string()
        throw UnexpectedResponseException(response, toast = "Content type does not match").also {
            Log.e("HandleResponse", contentType.toString())
            Log.e("HandleResponse", mediaType.toString())
        }
    }

    val body = response.body?.string()
    return if (response.isSuccessful) {
        try {
            jsonEncoder.fromJson<T>(body, type).also {
                Log.d("HandleResponse", "Response successful: $it")
            }
        } catch (e: JsonSyntaxException) {
            Log.e("HandleResponse", e.toString())
            throw UnexpectedResponseException(response, body, "Error parsing JSON")
        }
    } else {
        val problem: Problem
        try {
            problem = jsonEncoder.fromJson<Problem?>(body, problemType).also {
                Log.d("HandleResponse", "Response not successful (Problem: $it)")
            }
        } catch (e : Exception){
            throw UnexpectedResponseException(response, body, "An error has occurred").also {
                Log.e("HandleResponse", e.toString())
            }
        }
        throw UnexpectedResponseException(response, body, problem.detail).also {
            Log.e("HandleResponse", problem.detail)
        }
    }
}

internal fun List<SirenAction>.find(name : String) = this.first{ it.name == name}

internal fun List<SirenLink>.find(name : String) = this.first { it.rel.contains(name) }