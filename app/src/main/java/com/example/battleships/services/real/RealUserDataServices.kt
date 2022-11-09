package com.example.battleships.services.real

import com.example.battleships.dtos.*
import com.example.battleships.services.*
import com.example.battleships.services.buildRequest
import com.example.battleships.services.handleResponse
import com.example.battleships.utils.hypermedia.SirenAction
import com.example.battleships.utils.send
import com.google.gson.Gson
import okhttp3.OkHttpClient
import java.net.URL

class RealUserDataServices(
    private val httpClient: OkHttpClient,
    private val jsonEncoder: Gson
) {
    var userCreateAction: SirenAction? = null
    var userLoginAction: SirenAction? = null

    /**
     * Creates a new user with the given username and password.
     * @return The id of the newly created user, or null if needs [userCreateAction].
     */
    internal suspend fun createUser(
        username: String,
        password: String,
        mode: Mode,
        userCreateAction: SirenAction? = null,
    ): Int? {
        val userCreateAction: SirenAction = userCreateAction ?: this.userCreateAction ?: return null
        val url = userCreateAction.href.toURL()

        val request = buildRequest(
            Post(
                url,
                "{\"username\": \"$username\", \"password\": \"$password\"}"
            ), mode
        )

        return request.send(httpClient) { response ->
            handleResponse<CreateUserDto>(
                jsonEncoder,
                response,
                CreateUserDtoType.type
            )
        }.toUserId()
    }

    internal suspend fun doLogin(
        username: String,
        password: String,
        mode: Mode,
        userLoginAction: SirenAction? = null
    ): String? {
        val userLoginAction = userLoginAction ?: this.userLoginAction ?: return null
        val url = userLoginAction.href.toURL()

        val request = buildRequest(
            Post(
                url,
                "{\"username\": \"$username\", \"password\": \"$password\"}"
            ), mode
        )

        return request.send(httpClient) { response ->
            handleResponse<UserLoginDto>(jsonEncoder, response, UserLoginDtoType.type)
        }.toToken()
    }
}