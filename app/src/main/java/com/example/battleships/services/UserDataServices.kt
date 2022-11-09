package com.example.battleships.services

/**
 * This interface is responsible for providing the options that interact with the users.
 */
interface UserDataServices {
    fun createUser(username: String, password: String): Boolean

    suspend fun login(username: String, password: String): String?

    suspend fun getGameId(token: String): Int?
}