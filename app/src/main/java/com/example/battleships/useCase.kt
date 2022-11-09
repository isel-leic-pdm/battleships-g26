package com.example.battleships

import com.example.battleships.services.Mode
import com.example.battleships.services.real.RealGamesDataServices
import com.example.battleships.services.real.RealHomeDataServices
import com.example.battleships.services.real.RealUserDataServices

val homeServices = RealHomeDataServices()
val userServices = RealUserDataServices()
val gameServices = RealGamesDataServices()

suspend fun createUser(username: String, password: String, mode: Mode): Int {
    val userId = userServices.createUser(username, password, mode)
    if (userId == null) {
        val userCreateAction = homeServices.getCreateUserAction()
        return userServices.createUser(username, password, mode, userCreateAction)
            ?: throw IllegalStateException("User creation failed")
    }
    return userId
}

suspend fun getToken(username: String, password: String, mode: Mode): String {
    val token = userServices.getToken(username, password, mode)
    if (token == null) {
        val createTokenAction = homeServices.getCreateTokenAction()
        return userServices.getToken(username, password, mode, createTokenAction)
            ?: throw IllegalStateException("Token creation failed")
    }
    return token
}