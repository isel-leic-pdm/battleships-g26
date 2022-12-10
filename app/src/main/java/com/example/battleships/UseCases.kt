package com.example.battleships

import com.example.battleships.game.domain.game.Game
import com.example.battleships.rankings.GameRanking
import com.example.battleships.services.*
import com.example.battleships.services.real.RealGamesDataServices
import com.example.battleships.services.real.RealHomeDataServices
import com.example.battleships.services.real.RealUserDataServices
import pt.isel.daw.dawbattleshipgame.domain.board.Coordinate
import pt.isel.daw.dawbattleshipgame.domain.player.Player
import pt.isel.daw.dawbattleshipgame.domain.ship.Orientation
import pt.isel.daw.dawbattleshipgame.domain.ship.ShipType
import java.io.IOException

// TODO -> optimize how the links and actions are obtained
class UseCases(
    private val homeServices: HomeDataServices,
    private val userServices: UserDataServices,
    private val gameServices: GameDataServices
) {
    private val servicesAreReal: Boolean = (homeServices is RealHomeDataServices && userServices is RealUserDataServices
            && gameServices is RealGamesDataServices)

    suspend fun createUser(username: String, password: String, mode: Mode = Mode.AUTO): Int {
        val userId = userServices.createUser(username, password, mode)
        return if (servicesAreReal && userId == null) {
            homeServices as RealHomeDataServices
            val userCreateAction = homeServices.getCreateUserAction()
            userServices.createUser(username, password, mode, userCreateAction)
                ?: throw IllegalStateException("User creation failed")
        } else userId ?: throw IllegalStateException("User creation failed") // fake should never return null
    }

    suspend fun createToken(username: String, password: String, mode: Mode = Mode.AUTO): String? {
        val token = userServices.getToken(username, password, mode)
        return if (servicesAreReal && token == null) {
            homeServices as RealHomeDataServices
            val createTokenAction = homeServices.getCreateTokenAction()
            userServices.getToken(username, password, mode, createTokenAction)?.token
        } else token?.token ?: throw IllegalStateException("Token creation failed") // fake should never return null
    }

    /**
     * Creates a game.
     * @return the game id, or null if the game is still pending another player
     */
    suspend fun createGame(token: String, mode: Mode = Mode.AUTO): Int? {
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

    suspend fun fetchCurrentGameId(token: String, mode: Mode = Mode.AUTO): Int? {
        val gameId = gameServices.getCurrentGameId(token, null, mode)
        if (servicesAreReal && gameId is Either.Left) {
            gameServices as RealGamesDataServices
            userServices as RealUserDataServices
            homeServices as RealHomeDataServices
            val userHomeLink = homeServices.getUserHomeLink()
            val getCurrentGameIdLink = userServices.getCurrentGameIdLink(token, userHomeLink)
            val gameId = gameServices.getCurrentGameId(token, getCurrentGameIdLink, mode)
            if (gameId is Either.Left) {
                val createGameAction = userServices.getCreateGameAction(token, userHomeLink)
                gameServices.createGame(token, mode, createGameAction)
            } else throw IOException("Game creation failed")
        }
        return (gameId as Either.Right<Int?>).value
    }

    suspend fun fetchGame(token: String, mode: Mode = Mode.AUTO): Pair<Game, Player>? {
        val game = gameServices.getGame(token, mode = mode)
        if (servicesAreReal && game is Either.Left) {
            gameServices as RealGamesDataServices
            userServices as RealUserDataServices
            homeServices as RealHomeDataServices
            val userHomeLink = homeServices.getUserHomeLink()
            val getCurrentGameIdLink = userServices.getCurrentGameIdLink(token, userHomeLink)
            val game = gameServices.getGame(token, getCurrentGameIdLink, mode)
            gameServices.getGame(token, getGameLink, mode)
        }
        return (game as Either.Right<Pair<Game, Player>?>).value
    }

    @Throws(UnexpectedResponseException::class)
    suspend fun fetchRankings(mode: Mode = Mode.AUTO): GameRanking =
        homeServices.getRankings(mode)

    suspend fun setFleet(
        token: String,
        ships: List<Triple<ShipType, Coordinate, Orientation>>,
        mode: Mode = Mode.AUTO
    ): Boolean {
        if (!gameServices.setFleet(token, ships, null, mode)) return false
        return true
    }

    suspend fun placeShot(token: String, coordinate: Coordinate, mode: Mode = Mode.AUTO)
        = gameServices.placeShot(token, coordinate, null, mode)
}