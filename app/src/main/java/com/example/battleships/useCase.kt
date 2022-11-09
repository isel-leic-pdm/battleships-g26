package com.example.battleships

import com.example.battleships.services.real.RealGamesDataServices
import com.example.battleships.services.real.RealHomeDataServices
import com.example.battleships.services.real.RealUserDataServices

val homeServices = RealHomeDataServices()
val userServices = RealUserDataServices()
val gameServices = RealGamesDataServices()

fun createUser(username: String, password: String): Int {
    val userId = userServices.createUser(username, password)
    if (userId == null) {
        val userCreateAction = homeServices.ensureUserCreateAction()
        return userServices.createUser(username, password, userCreateAction) ?: throw IllegalStateException("User creation failed")
    }
}