package com.example.battleships.services

import com.example.battleships.user_home.UserHome
import com.example.battleships.utils.hypermedia.SirenAction
import com.example.battleships.utils.hypermedia.SirenLink
/**
 * This interface is responsible for providing the options that interact with the users.
 */
interface UserDataServices {
    suspend fun createUser(username: String, password: String, mode: Mode, userCreateAction: SirenAction? = null): Int?

    class Response(val token: String?)
    suspend fun getToken(username: String, password: String, mode: Mode, createTokenAction: SirenAction? = null): Response?
    suspend fun getHome(token: String, mode: Mode, userHomeLink: SirenLink? = null): UserHome
}