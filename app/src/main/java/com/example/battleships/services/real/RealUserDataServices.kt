package com.example.battleships.services.real

import com.example.battleships.dtos.*
import com.example.battleships.services.*
import com.example.battleships.services.models.UserCreateOutputModel
import com.example.battleships.home.UserHome
import com.example.battleships.rankings.rankings
import com.example.battleships.utils.hypermedia.*
import com.example.battleships.utils.send
import com.google.gson.Gson
import okhttp3.OkHttpClient
import pt.isel.daw.dawbattleshipgame.domain.player.UserRanking
import java.net.URL

class RealUserDataServices(
    private val httpClient: OkHttpClient,
    private val jsonEncoder: Gson
): UserDataServices {
    private var userCreateAction: SirenAction? = null
    private var createTokenAction: SirenAction? = null
    private var userHomeLink: SirenLink? = null
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
        userCreateAction: SirenAction?,
    ): Either<ApiException, Int> {
        val auxUserCreateAction: SirenAction = userCreateAction?.also { this.userCreateAction = it }
            ?: this.userCreateAction ?: return Either.Left(UnresolvedActionException())
        val url = auxUserCreateAction.href.toApiURL()

        val request = buildRequest(
            Post(url, UserCreateOutputModel(username, password).toJson(jsonEncoder)), null, mode
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
    ): Either<ApiException, String> {
        val auxCreateTokenAction = createTokenAction?.also { this.createTokenAction = it }
            ?: this.createTokenAction ?: return Either.Left(UnresolvedActionException())
        val url = auxCreateTokenAction.href.toApiURL()

        val request = buildRequest(
            Post(url, UserCreateOutputModel(username, password).toJson(jsonEncoder)), null, mode
        )
        return try {
            val createTokenDto = request.send(httpClient) { response ->
                handleResponse<UserLoginDto>(jsonEncoder, response, UserLoginDtoType.type, SirenMediaType)
            }
            userHomeLink = extractUserHomeLink(createTokenDto) ?: throw UnresolvedLinkException()
            Either.Right(createTokenDto.toToken())
        } catch (e: ApiException) {
            Either.Left(e)
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

        println(userHomeDto)
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