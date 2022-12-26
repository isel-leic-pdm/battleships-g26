package com.example.battleships.services.fake

import com.example.battleships.services.*
import com.example.battleships.home.UserHome
import com.example.battleships.utils.hypermedia.SirenAction
import com.example.battleships.utils.hypermedia.SirenLink

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
        return Either.Right(id)
    }

    override suspend fun getToken(
        username: String,
        password: String,
        mode: Mode,
        createTokenAction: SirenAction?
    ): Either<ApiException, String?> {
        return Either.Right(users[UserCredentials(username, password)]?.let { tokens[it] }
            ?: throw UnexpectedResponseException()
        )
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
        TODO("Not yet implemented")
    }

}