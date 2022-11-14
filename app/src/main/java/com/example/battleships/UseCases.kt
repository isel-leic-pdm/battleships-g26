package com.example.battleships

import com.example.battleships.services.GameDataServices
import com.example.battleships.services.HomeDataServices
import com.example.battleships.services.Mode
import com.example.battleships.services.UserDataServices
import com.example.battleships.services.real.RealGamesDataServices
import com.example.battleships.services.real.RealHomeDataServices
import com.example.battleships.services.real.RealUserDataServices
import com.example.battleships.utils.hypermedia.SirenAction

// TODO -> optimize how the links and actions are obtained
class UseCases(
    private val homeServices: HomeDataServices,
    private val userServices: UserDataServices,
    private val gameServices: GameDataServices
) {
    private val servicesAreReal: Boolean = (homeServices is RealHomeDataServices && userServices is RealUserDataServices
            && gameServices is RealGamesDataServices)

    suspend fun createUser(username: String, password: String, mode: Mode): Int? {
        val userId = userServices.createUser(username, password, mode)
        if (servicesAreReal && userId == null) {
            homeServices as RealHomeDataServices
            val userCreateAction = homeServices.getCreateUserAction()
            return userServices.createUser(username, password, mode, userCreateAction)
                ?: throw IllegalStateException("User creation failed")
        }
        return userId
    }

    suspend fun getToken(username: String, password: String, mode: Mode): String? {
        val token = userServices.getToken(username, password, mode)
        if (servicesAreReal && token == null) {
            homeServices as RealHomeDataServices
            val createTokenAction = homeServices.getCreateTokenAction()
            return userServices.getToken(username, password, mode, createTokenAction)
                ?: throw IllegalStateException("Token creation failed")
        }
        return token
    }

    suspend fun createGame(token: String, mode: Mode) {
        val gameId = gameServices.createGame(token, mode)
        if (servicesAreReal && gameId == null) {
            gameServices as RealGamesDataServices
            userServices as RealUserDataServices
            homeServices as RealHomeDataServices
            val userHomeLink = homeServices.getUserHomeLink()
            val createGameAction = userServices.getCreateGameAction(token, userHomeLink)
            gameServices.createGame(token, mode, createGameAction)
        }
    }
}