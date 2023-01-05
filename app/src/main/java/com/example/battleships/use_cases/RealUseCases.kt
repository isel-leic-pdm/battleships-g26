package com.example.battleships.use_cases

import android.util.Log
import com.example.battleships.game.domain.game.Configuration
import com.example.battleships.game.domain.game.Game
import com.example.battleships.game.domain.game.ShotsList
import com.example.battleships.home.UserHome
import com.example.battleships.rankings.UserRanking
import com.example.battleships.rankings.UserStats
import com.example.battleships.services.*
import com.example.battleships.services.real.RealGamesDataServices
import com.example.battleships.services.real.RealHomeDataServices
import com.example.battleships.services.real.RealUserDataServices
import pt.isel.daw.dawbattleshipgame.domain.player.Player
import pt.isel.daw.dawbattleshipgame.domain.ship.Orientation
import pt.isel.daw.dawbattleshipgame.domain.ship.ShipType

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
            val resUserId = userServices.createUser(username, password, mode, userCreateAction)
            return@getValueOrExecute getValueOrThrow(resUserId)
        }
    }

    override suspend fun createToken(username: String, password: String, mode: Mode): String {
        val token = userServices.getToken(username, password, mode)
        return getValueOrExecute(token) {
            val createTokenAction = homeServices.getCreateTokenAction()
            val resToken = userServices.getToken(username, password, mode, createTokenAction)
            return@getValueOrExecute getValueOrThrow(resToken)
        }
    }

    /**
     * Creates a game.
     * @return the game id, or null if the game is still pending another player
     */
    override suspend fun createGame(token: String, mode: Mode, configuration: Configuration?): Boolean {
        val gameId = gameServices.createGame(token, mode, configuration = configuration)
        return getValueOrExecute(gameId) {
            val userHomeLink = homeServices.getUserHomeLink()
            val createGameAction = userServices.getCreateGameAction(token, userHomeLink)
            val game = gameServices.createGame(token, mode, createGameAction, configuration)
            return@getValueOrExecute getValueOrThrow(game)
        }
    }

    override suspend fun fetchCurrentGameId(token: String, mode: Mode): Int? {
        val gameId = gameServices.getCurrentGameId(token, null, mode)
        return getValueOrExecute(gameId) {
            val userHomeLink = homeServices.getUserHomeLink()
            val getCurrentGameIdLink = userServices.getCurrentGameIdLink(token, userHomeLink)
            val resGameId = gameServices.getCurrentGameId(token, getCurrentGameIdLink, mode)
            return@getValueOrExecute getValueOrThrow(resGameId)
        }
    }

    override suspend fun fetchGame(token: String, mode: Mode): Pair<Game, Player>? {
        val game = gameServices.getGame(token, mode = mode)
        return getValueOrExecute(game) {
            val userHomeLink = homeServices.getUserHomeLink()
            val getCurrentGameIdLink = userServices.getCurrentGameIdLink(token, userHomeLink)
            val resGame = gameServices.getGame(token, getCurrentGameIdLink, mode)
            return@getValueOrExecute getValueOrThrow(resGame)
        }
    }

    @Throws(UnexpectedResponseException::class)
    override suspend fun fetchRankings(mode: Mode): UserRanking =
        homeServices.getRankings(mode)

    override suspend fun setFleet(
        token: String,
        ships: List<Triple<ShipType, com.example.battleships.game.domain.board.Coordinate, Orientation>>,
        mode: Mode
    ): Boolean {
        val result = gameServices.setFleet(token, ships, null, mode)
        return getValueOrExecute(result) {
            val userHomeLink = homeServices.getUserHomeLink()
            val getCurrentGameIdLink = userServices.getCurrentGameIdLink(token, userHomeLink)
            val getGameLink = gameServices.getGameLink(token, getCurrentGameIdLink)
            val placeShipsAction = gameServices.getSetFleetAction(token, getGameLink)
            val res = gameServices.setFleet(token, ships, placeShipsAction, mode)
            return@getValueOrExecute getValueOrThrow(res)
        }
    }

    override suspend fun placeShots(token: String, shots: ShotsList, mode: Mode): Boolean {
        val result = gameServices.placeShots(token, shots, null, mode)
        return getValueOrExecute(result) {
            val userHomeLink = homeServices.getUserHomeLink()
            val getCurrentGameIdLink = userServices.getCurrentGameIdLink(token, userHomeLink)
            val getGameLink = gameServices.getGameLink(token, getCurrentGameIdLink)
            val placeShotAction = gameServices.getPlaceShotAction(token, getGameLink)
            val res = gameServices.placeShots(token, shots, placeShotAction, mode)
            return@getValueOrExecute getValueOrThrow(res)
        }
    }

    @Throws(UnexpectedResponseException::class)
    override suspend fun fetchServerInfo(mode: Mode) =
        homeServices.getServerInfo(mode)

    override suspend fun getUserById(id: Int, mode: Mode): UserStats =
        homeServices.getUserById(id, mode) ?: throw UnexpectedResponseException(toast = "User not found")

    override suspend fun getUserHome(token: String, mode: Mode) : UserHome{
        val userHomeLink = homeServices.getUserHomeLink()
        return userServices.getUserHome(token, Mode.AUTO, userHomeLink)
    }

    override suspend fun getHome() = homeServices.getHome()

    private suspend fun <T> getValueOrExecute(either: Either<ApiException, T>, onEitherLeft: suspend () -> T): T {
        return when (either) {
            is Either.Left -> onEitherLeft()
            is Either.Right -> either.value
        }
    }
}
