package com.example.battleships.services.fake

import android.util.Log
import com.example.battleships.services.*
import com.example.battleships.home.UserHome
import com.example.battleships.rankings.UserStats
import com.example.battleships.utils.hypermedia.SirenAction
import com.example.battleships.utils.hypermedia.SirenLink
import pt.isel.daw.dawbattleshipgame.domain.player.UserRanking

private data class UserCredentials(val username: String, val password: String)

class FakeUserDataServices : UserDataServices {
    private val users = mutableMapOf(
        UserCredentials("", "") to 1
    )

    /** Associates each existing userId to its token */
    private val tokens = mutableMapOf(
        1 to "",
    )

    override suspend fun createUser(
        username: String,
        password: String,
        mode: Mode,
        userCreateAction: SirenAction?
    ): Either<ApiException, Int> {
        if (users.containsKey(UserCredentials(username, password))) throw UnexpectedResponseException()
        val id = users.size + 1
        users[UserCredentials(username, password)] = id
        tokens[id] = ((1..99999).random() * 0.3).toString()
        return Either.Right(id)
    }

    override suspend fun getToken(
        username: String,
        password: String,
        mode: Mode,
        createTokenAction: SirenAction?
    ): Either<ApiException, String> {
        val userId = users.filter { it.key.username == username && it.key.password == password }.values.firstOrNull()
            ?: throw UnexpectedResponseException()
        val token = tokens[userId] ?: throw UnexpectedResponseException()
        return Either.Right(token)
    }

    /*
    override suspend fun getToken(
        username: String,
        password: String,
        mode: Mode,
        createTokenAction: SirenAction?
    ): Either<Unit, String> {
        throw UnexpectedResponseException()
    }
     */

    override suspend fun getUserHome(token: String, mode: Mode, userHomeLink: SirenLink?): UserHome {
        val userId = tokens.filter { it.value == token }.keys.firstOrNull() ?: throw UnexpectedResponseException()
        val user = users.filter { it.value == userId }.keys.firstOrNull() ?: throw UnexpectedResponseException()
        return UserHome(userId, user.username)
    }
}