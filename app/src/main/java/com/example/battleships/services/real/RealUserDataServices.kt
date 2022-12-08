package com.example.battleships.services.real

import com.example.battleships.dtos.*
import com.example.battleships.services.*
import com.example.battleships.user_home.UserHome
import com.example.battleships.utils.hypermedia.SirenAction
import com.example.battleships.utils.hypermedia.SirenEntity
import com.example.battleships.utils.hypermedia.SirenLink
import com.example.battleships.utils.hypermedia.toApiURL
import com.example.battleships.utils.send
import com.google.gson.Gson
import okhttp3.OkHttpClient

class RealUserDataServices(
    private val httpClient: OkHttpClient,
    private val jsonEncoder: Gson
): UserDataServices {
    var userCreateAction: SirenAction? = null
    var createTokenAction: SirenAction? = null
    var userHomeLink: SirenLink? = null
    private var createGameAction: SirenAction? = null
    private var getCurrentGameIdLink: SirenLink? = null

    /**
     * Creates a new user with the given username and password.
     * @return The id of the newly created user, or null if needs [userCreateAction].
     */
    override suspend fun createUser(
        username: String,
        password: String,
        mode: Mode,
        newUserCreateAction: SirenAction?,
    ): Int? {
        val userCreateAction: SirenAction = newUserCreateAction?.also { userCreateAction = it }
            ?: this.userCreateAction ?: return null
        val url = userCreateAction.href.toApiURL()

        val request = buildRequest(
            Post(
                url,
                "{\n" +
                        "    \"username\": \"$username\",\n" +
                        "    \"password\": \"$password\"\n" +
                        "}"
            ), mode
        )
        val createUserDto = request.send(httpClient) { response ->
            handleResponse<CreateUserDto>(
                jsonEncoder,
                response,
                CreateUserDtoType.type
            )
        }
        createTokenAction = getCreateTokenAction(createUserDto) ?: throw UnresolvedLinkException()
        return createUserDto.toUserId()
    }

    override suspend fun getToken(
        username: String,
        password: String,
        mode: Mode,
        newCreateTokenAction: SirenAction?
    ): String? {
        val createTokenAction = newCreateTokenAction?.also { createTokenAction = it }
            ?: this.createTokenAction ?: return null
        val url = createTokenAction.href.toApiURL()

        val request = buildRequest(
            Post(
                url,
                "{\n" +
                        "    \"username\": \"$username\",\n" +
                        "    \"password\": \"$password\"\n" +
                        "}"
            ), mode
        )
        val createTokenDto = request.send(httpClient) { response ->
            handleResponse<UserLoginDto>(jsonEncoder, response, UserLoginDtoType.type)
        }
        userHomeLink = getUserHomeLink(createTokenDto) ?: throw UnresolvedLinkException()
        return createTokenDto.toToken()
    }

    override suspend fun getHome(
        token: String,
        mode: Mode,
        userHomeLink: SirenLink?
    ): UserHome {
        val userHomeLink = userHomeLink ?: this.userHomeLink ?: throw UnresolvedLinkException()
        val url = userHomeLink.href.toApiURL()

        val request = buildRequest(Get(url), mode)
        val userHomeDto = request.send(httpClient) { response ->
            handleResponse<UserHomeDto>(jsonEncoder, response, UserHomeDtoType.type)
        }
        createGameAction = getCreateGameAction(userHomeDto) ?: throw UnresolvedLinkException()
        getCurrentGameIdLink = getCurrentGameIdLink(userHomeDto) ?: throw UnresolvedLinkException()
        return userHomeDto.toUserHome()
    }

    private fun getUserHomeLink(createTokenDto: SirenEntity<UserLoginDtoProperties>) =
        createTokenDto.links?.find { it.rel.contains("user-home") }

    private fun getCreateTokenAction(dto: CreateUserDto) =
        dto.actions?.find { it.name == "create-token" }

    private fun getCreateGameAction(userHomeDto: SirenEntity<UserHomeDtoProperties>) =
        userHomeDto.actions?.find { it.name == "create-game" }

    private fun getCurrentGameIdLink(userHomeDto: SirenEntity<UserHomeDtoProperties>) =
        userHomeDto.links?.find { it.rel.contains("game-id") }

    suspend fun getCreateGameAction(token: String, newUserHomeLink: SirenLink): SirenAction {
        if (createGameAction == null) {
            getHome(token, Mode.FORCE_REMOTE, newUserHomeLink)
            return createGameAction ?: throw UnresolvedLinkException()
        }
        return createGameAction ?: throw UnresolvedLinkException()
    }
}