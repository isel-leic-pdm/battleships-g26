package com.example.battleships.services.fake

import com.example.battleships.services.Mode
import com.example.battleships.services.UnexpectedResponseException
import com.example.battleships.services.UserDataServices
import com.example.battleships.user_home.UserHome
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
    ): Int {
        if (users.containsKey(UserCredentials(username, password))) throw UnexpectedResponseException()
        val id = users.size + 1
        users[UserCredentials(username, password)] = id
        return id
    }

    override suspend fun getToken(
        username: String,
        password: String,
        mode: Mode,
        createTokenAction: SirenAction?
    ) = UserDataServices.Response(users[UserCredentials(username, password)]?.let { tokens[it] }
        ?: throw UnexpectedResponseException())

    /*
    override suspend fun getToken(
        username: String,
        password: String,
        mode: Mode,
        createTokenAction: SirenAction?
    ): String {
        throw UnexpectedResponseException()
    }
     */

    override suspend fun getUserHome(token: String, mode: Mode, userHomeLink: SirenLink?): UserHome {
        TODO("Not yet implemented")
    }

}