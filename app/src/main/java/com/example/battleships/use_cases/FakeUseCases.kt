package com.example.battleships.use_cases

import com.example.battleships.game.domain.game.Game
import com.example.battleships.rankings.GameRanking
import com.example.battleships.services.*
import com.example.battleships.services.fake.FakeGameDataServices
import com.example.battleships.services.fake.FakeHomeDataServices
import com.example.battleships.services.fake.FakeUserDataServices
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
class FakeUseCases(
    private val homeServices: FakeHomeDataServices,
    private val userServices: FakeUserDataServices,
    private val gameServices: FakeGameDataServices
): UseCases {

    override suspend fun createUser(username: String, password: String, mode: Mode): Int {
        val userId = userServices.createUser(username, password, mode)
        when (userId) {
            is Either.Left -> throw java.lang.IllegalStateException("Create game should not return Either.Left")
            is Either.Right -> return userId.value
        }
    }

    override suspend fun createToken(username: String, password: String, mode: Mode): String? {
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
    override suspend fun createGame(token: String, mode: Mode): Int? {
        val gameId = gameServices.createGame(token, mode)
        when (gameId) {
            is Either.Left -> throw java.lang.IllegalStateException("Create game should not return Either.Left")
            is Either.Right -> return gameId.value
        }
    }

    override suspend fun fetchCurrentGameId(token: String, mode: Mode): Int? {
        val gameId = gameServices.getCurrentGameId(token, null, mode)
        when (gameId) {
            is Either.Left -> throw java.lang.IllegalStateException("Create game should not return Either.Left")
            is Either.Right -> return gameId.value
        }
    }

    override suspend fun fetchGame(token: String, mode: Mode): Pair<Game, Player>? {
        val game = gameServices.getGame(token, mode = mode)
        when (game) {
            is Either.Left -> throw java.lang.IllegalStateException("Create game should not return Either.Left")
            is Either.Right -> return game.value
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
        val result = gameServices.setFleet(token, ships, mode = mode)
        when (result) {
            is Either.Left -> throw java.lang.IllegalStateException("Create game should not return Either.Left")
            is Either.Right -> return result.value
        }
    }

    override suspend fun placeShot(token: String, coordinate: Coordinate, mode: Mode): Boolean {
        val result = gameServices.placeShot(token, coordinate, mode = mode)
        when (result) {
            is Either.Left -> throw java.lang.IllegalStateException("Create game should not return Either.Left")
            is Either.Right -> return result.value
        }
    }
}