package com.example.battleships.services.fake

import com.example.battleships.services.Mode
import com.example.battleships.services.UserDataServices
import com.example.battleships.user_home.UserHome
import com.example.battleships.utils.hypermedia.SirenAction
import com.example.battleships.utils.hypermedia.SirenLink

private data class UserCredentials(val username: String, val password: String)

class FakeUserDataServices : UserDataServices {
    private val users = mutableMapOf<UserCredentials, Int>(
        UserCredentials("", "") to 1
    )
    private val tokens = mutableMapOf<Int, String>(
        1 to "",
    )

    override suspend fun createUser(
        username: String,
        password: String,
        mode: Mode,
        userCreateAction: SirenAction?
    ): Int? {
        TODO("Not yet implemented")
    }

    override suspend fun getToken(
        username: String,
        password: String,
        mode: Mode,
        createTokenAction: SirenAction?
    ): String? {
        return users[UserCredentials(username, password)]?.let { tokens[it] }
    }

    override suspend fun getHome(token: String, mode: Mode, userHomeLink: SirenLink?): UserHome {
        TODO("Not yet implemented")
    }

}