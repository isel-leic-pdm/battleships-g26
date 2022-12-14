package com.example.battleships.services.real

import com.example.battleships.dtos.*
import com.example.battleships.services.*
import com.example.battleships.user_home.UserHome
import com.example.battleships.utils.hypermedia.*
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

    private data class UserCreateModel(val username: String, val password: String){
        fun toJson(encoder : Gson): String = encoder.toJson(this)
    }

    /**
     * Creates a new user with the given username and password.
     * @return The id of the newly created user, or null if needs [userCreateAction].
     */
    override suspend fun createUser(
        username: String,
        password: String,
        mode: Mode,
        userCreateAction: SirenAction?,
    ): Either<Unit, Int> {
        val auxUserCreateAction: SirenAction = userCreateAction?.also { this.userCreateAction = it }
            ?: this.userCreateAction ?: return Either.Left(Unit)
        val url = auxUserCreateAction.href.toApiURL()

        val request = buildRequest(
            Post(url, UserCreateModel(username, password).toJson(jsonEncoder)), null, mode
        )
        val createUserDto = request.send(httpClient) { response ->
            handleResponse<CreateUserDto>(
                jsonEncoder,
                response,
                CreateUserDtoType.type,
                SirenMediaType
            )
        }
        createTokenAction = extractCreateTokenAction(createUserDto) ?: throw UnresolvedLinkException()
        return Either.Right(createUserDto.toUserId())
    }

    override suspend fun getToken(
        username: String,
        password: String,
        mode: Mode,
        createTokenAction: SirenAction?
    ): Either<Unit, String?> {
        val auxCreateTokenAction = createTokenAction?.also { this.createTokenAction = it }
            ?: this.createTokenAction ?: return Either.Left(Unit)
        val url = auxCreateTokenAction.href.toApiURL()

        val request = buildRequest(
            Post(url, UserCreateModel(username, password).toJson(jsonEncoder)), null, mode
        )
        try {
            val createTokenDto = request.send(httpClient) { response ->
                handleResponse<UserLoginDto>(jsonEncoder, response, UserLoginDtoType.type, SirenMediaType)
            }
            userHomeLink = extractUserHomeLink(createTokenDto) ?: throw UnresolvedLinkException()
            return Either.Right(createTokenDto.toToken())
        } catch (e: UnexpectedResponseException) {
            if (e.response?.code == 403) // if credentials were wrong
                return Either.Right(null)
            throw e
        }
    }

    override suspend fun getUserHome(
        token: String,
        mode: Mode,
        userHomeLink: SirenLink?
    ): UserHome {
        val auxUserHomeLink = userHomeLink ?: this.userHomeLink ?: throw UnresolvedLinkException()
        val url = auxUserHomeLink.href.toApiURL()

        val request = buildRequest(Get(url), token, mode)
        val userHomeDto = request.send(httpClient) { response ->
            handleResponse<UserHomeDto>(jsonEncoder, response, UserHomeDtoType.type, SirenMediaType)
        }
        createGameAction = extractCreateGameAction(userHomeDto) ?: throw UnresolvedLinkException()
        getCurrentGameIdLink = extractCurrentGameIdLink(userHomeDto) ?: throw UnresolvedLinkException()
        return userHomeDto.toUserHome()
    }

    private fun extractUserHomeLink(createTokenDto: SirenEntity<UserLoginDtoProperties>) =
        createTokenDto.links?.find { it.rel.contains("user-home") }

    private fun extractCreateTokenAction(dto: CreateUserDto) =
        dto.actions?.find { it.name == "create-token" }

    private fun extractCreateGameAction(userHomeDto: SirenEntity<UserHomeDtoProperties>) =
        userHomeDto.actions?.find { it.name == "create-game" }

    private fun extractCurrentGameIdLink(userHomeDto: SirenEntity<UserHomeDtoProperties>) =
        userHomeDto.links?.find { it.rel.contains("game-id") }

    suspend fun getCreateGameAction(token: String, newUserHomeLink: SirenLink): SirenAction {
        if (createGameAction == null) {
            getUserHome(token, Mode.FORCE_REMOTE, newUserHomeLink)
            return createGameAction ?: throw UnresolvedLinkException()
        }
        return createGameAction ?: throw UnresolvedLinkException()
    }

    suspend fun getCurrentGameIdLink(token: String, newUserHomeLink: SirenLink): SirenLink {
        if (getCurrentGameIdLink == null) {
            getUserHome(token, Mode.FORCE_REMOTE, newUserHomeLink)
            return getCurrentGameIdLink ?: throw UnresolvedLinkException()
        }
        return getCurrentGameIdLink ?: throw UnresolvedLinkException()
    }
}