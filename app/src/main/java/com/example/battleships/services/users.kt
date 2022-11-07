package com.example.battleships.services

import com.example.battleships.dtos.*
import com.example.battleships.utils.hypermedia.SirenAction
import com.example.battleships.utils.send
import java.net.URL

/**
 * @see rankingsLink
 */
internal var userCreateAction: SirenAction? = null
internal var userLoginAction: SirenAction? = null

internal suspend fun createUserInternal(requestParams: RequestParams, username: String, password: String): Boolean {
    val createUserURL: URL = ensureUserCreateLink(requestParams)

    val request = buildRequest(Post(createUserURL,
        "{\"username\": \"$username\", \"password\": \"$password\"}"), requestParams.mode)

    return request.send(requestParams.client) { response ->
        handleResponse<CreateUserDto>(requestParams.jsonEncoder, response, CreateUserDtoType.type)
    }.toQuoteList()
}

internal suspend fun doLoginInternal(requestParams: RequestParams, username: String, password: String): String {
    val doLoginURL: URL = ensureUserLoginLink(requestParams)

    val request = buildRequest(Post(doLoginURL,
        "{\"username\": \"$username\", \"password\": \"$password\"}"), requestParams.mode)

    return request.send(requestParams.client) { response ->
        handleResponse<UserLoginDto>(requestParams.jsonEncoder, response, UserLoginDtoType.type)
    }.toToken()
}

private fun getServerInfoLink(home: HomeDto) =
    home.links?.find { it.rel.contains("server-info") }

internal suspend fun ensureUserCreateLink(requestParams: RequestParams): URL {
    if (userCreateAction == null) {
        getHome(requestParams)
    }
    val action = userCreateAction ?: throw UnresolvedLinkException()
    return action.href.toURL()
}

internal suspend fun ensureUserLoginLink(requestParams: RequestParams): URL {
    if (userLoginAction == null) {
        getHome(requestParams)
    }
    val action = userLoginAction ?: throw UnresolvedLinkException()
    return action.href.toURL()
}
