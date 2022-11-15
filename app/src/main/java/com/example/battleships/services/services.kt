package com.example.battleships.services

import com.example.battleships.services.Mode.*
import okhttp3.Response

/**
 * Used to identify how implementations SHOULD behave:
 * - [FORCE_REMOTE] is used to indicate that the operation MUST try to access
 * the remote data source
 * - [FORCE_LOCAL] is usd to indicate that the operation SHOULD only use the
 * the local version of the data, if available
 * - [AUTO] states that the selection of which data to use is left to the
 * implementation.
 */
enum class Mode { FORCE_REMOTE, FORCE_LOCAL, AUTO }

abstract class ApiException(msg: String) : Exception(msg)

/**
 * Exception throw when a required navigation link could not be found by
 * the service implementation in the APIs responses.
 */
class UnresolvedLinkException(msg: String = "") : ApiException(msg)

/**
 * Exception throw when an unexpected response was received from the API.
 */
class UnexpectedResponseException(
    val response: Response? = null
) : ApiException("Unexpected ${response?.code} response from the API.")
