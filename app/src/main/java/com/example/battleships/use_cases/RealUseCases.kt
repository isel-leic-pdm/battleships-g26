package com.example.battleships.use_cases

import com.example.battleships.game.domain.game.Game
import com.example.battleships.rankings.GameRanking
import com.example.battleships.services.*
import com.example.battleships.services.real.RealGamesDataServices
import com.example.battleships.services.real.RealHomeDataServices
import com.example.battleships.services.real.RealUserDataServices
import com.example.battleships.use_cases.UseCases
import pt.isel.daw.dawbattleshipgame.domain.board.Coordinate
import pt.isel.daw.dawbattleshipgame.domain.player.Player
import pt.isel.daw.dawbattleshipgame.domain.ship.Orientation
import pt.isel.daw.dawbattleshipgame.domain.ship.ShipType
import java.io.IOException

// TODO -> optimize how the links and actions are obtained
class RealUseCases(
    private val homeServices: RealHomeDataServices,
    private val userServices: RealUserDataServices,
    private val gameServices: RealGamesDataServices
): UseCases {

    override suspend fun createUser(username: String, password: String, mode: Mode): Int {
        val userId = userServices.createUser(username, password, mode)
        return getValueOrExecute(userId) {
            val userCreateAction = homeServices.getCreateUserAction()
            val userId = userServices.createUser(username, password, mode, userCreateAction)
            return@getValueOrExecute getValueOrThrow(userId)
        }
    }

    override suspend fun createToken(username: String, password: String, mode: Mode = Mode.AUTO): String? {
        val token = userServices.getToken(username, password, mode)
        return getValueOrExecute(token) {
            val createTokenAction = homeServices.getCreateTokenAction()
            val token = userServices.getToken(username, password, mode, createTokenAction)
            return@getValueOrExecute getValueOrExecute(token)
        }
    }

    /**
     * Creates a game.
     * @return the game id, or null if the game is still pending another player
     */
    override suspend fun createGame(token: String, mode: Mode): Int? {
        val gameId = gameServices.createGame(token, mode)
        if (servicesAreReal && gameId == null) {
            val userHomeLink = homeServices.getUserHomeLink()
            val createGameAction = userServices.getCreateGameAction(token, userHomeLink)
            gameServices.createGame(token, mode, createGameAction)
        }
    }

    override suspend fun fetchCurrentGameId(token: String, mode: Mode): Int? {
        val gameId = gameServices.getCurrentGameId(token, null, mode)
        return getValueOrExecute(gameId) {
            val userHomeLink = homeServices.getUserHomeLink()
            val getCurrentGameIdLink = userServices.getCurrentGameIdLink(token, userHomeLink)
            val gameId = gameServices.getCurrentGameId(token, getCurrentGameIdLink, mode)
            return@getValueOrExecute getValueOrThrow(gameId)
        }
    }

    override suspend fun fetchGame(token: String, mode: Mode): Pair<Game, Player>? {
        val game = gameServices.getGame(token, mode = mode)
        return getValueOrExecute(game) {
            val userHomeLink = homeServices.getUserHomeLink()
            val getCurrentGameIdLink = userServices.getCurrentGameIdLink(token, userHomeLink)
            val game = gameServices.getGame(token, getCurrentGameIdLink, mode)
            return@getValueOrExecute getValueOrThrow(game)
        }
    }

    @Throws(UnexpectedResponseException::class)
    override suspend fun fetchRankings(mode: Mode): GameRanking =
        homeServices.getRankings(mode)

    override suspend fun setFleet(
        token: String,
        ships: List<Triple<ShipType, Coordinate, Orientation>>,
        mode: Mode
    ): Boolean {
        val result = gameServices.setFleet(token, ships, null, mode)
        return getValueOrExecute(result) {
            val userHomeLink = homeServices.getUserHomeLink()
            val getCurrentGameIdLink = userServices.getCurrentGameIdLink(token, userHomeLink)
            val getGameLink = gameServices.getGameLink(token, getCurrentGameIdLink)
            val placeShipsAction = gameServices.getSetFleetAction(token, getGameLink)
            val result = gameServices.setFleet(token, ships, placeShipsAction, mode)
            return@getValueOrExecute getValueOrThrow(result)
        }
    }

    override suspend fun placeShot(token: String, coordinate: Coordinate, mode: Mode): Boolean {
        val result = gameServices.placeShot(token, coordinate, null, mode)
        return getValueOrExecute(result) {
            val userHomeLink = homeServices.getUserHomeLink()
            val getCurrentGameIdLink = userServices.getCurrentGameIdLink(token, userHomeLink)
            val getGameLink = gameServices.getGameLink(token, getCurrentGameIdLink)
            val placeShotAction = gameServices.getPlaceShotAction(token, getGameLink)
            val result = gameServices.placeShot(token, coordinate, placeShotAction, mode)
            return@getValueOrExecute getValueOrThrow(result)
        }
    }

    private fun <T> getValueOrThrow(either: Either<Unit, T>): T {
        when (either) {
            is Either.Right -> return either.value
            is Either.Left -> throw IOException()
        }
    }

    private suspend fun <T> getValueOrExecute(either: Either<Unit, T>, onEitherLeft: suspend () -> T): T {
        return when (either) {
            is Either.Left -> onEitherLeft()
            is Either.Right -> either.value
        }
    }

}
