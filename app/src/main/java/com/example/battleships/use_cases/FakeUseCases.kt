package com.example.battleships.use_cases

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.battleships.game.domain.game.Game
import com.example.battleships.info.ServerInfo
import com.example.battleships.rankings.GameRanking
import com.example.battleships.services.*
import com.example.battleships.services.fake.FakeGameDataServices
import com.example.battleships.services.fake.FakeHomeDataServices
import com.example.battleships.services.fake.FakeUserDataServices
import pt.isel.daw.dawbattleshipgame.domain.board.Coordinate
import pt.isel.daw.dawbattleshipgame.domain.player.Player
import pt.isel.daw.dawbattleshipgame.domain.ship.Orientation
import pt.isel.daw.dawbattleshipgame.domain.ship.ShipType

class FakeUseCases(
    private val homeServices: FakeHomeDataServices,
    private val userServices: FakeUserDataServices,
    private val gameServices: FakeGameDataServices
): UseCases {

    override suspend fun createUser(username: String, password: String, mode: Mode): Int {
        val userId = userServices.createUser(username, password, mode)
        return getValueOrThrow(userId)
    }

    override suspend fun createToken(username: String, password: String, mode: Mode): String {
        val token = userServices.getToken(username, password, mode)
        return getValueOrThrow(token)
    }

    /**
     * Creates a game.
     * @return the game id, or null if the game is still pending another player
     */
    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun createGame(token: String, mode: Mode): Boolean {
        val gameId = gameServices.createGame(token, mode)
        return getValueOrThrow(gameId)
    }

    override suspend fun fetchCurrentGameId(token: String, mode: Mode): Int? {
        val gameId = gameServices.getCurrentGameId(token, null, mode)
        return getValueOrThrow(gameId)
    }

    override suspend fun fetchGame(token: String, mode: Mode): Pair<Game, Player>? {
        val game = gameServices.getGame(token, mode = mode)
        return getValueOrThrow(game)
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
        return getValueOrThrow(result)
    }

    override suspend fun placeShot(token: String, coordinate: Coordinate, mode: Mode): Boolean {
        val result = gameServices.placeShot(token, coordinate, mode = mode)
        return getValueOrThrow(result)
    }

    override suspend fun fetchServerInfo(mode: Mode): ServerInfo {
        TODO("Not yet implemented")
    }

    private fun <T> getValueOrThrow(either: Either<ApiException, T?>): T =
        getValueOrThrow(either)
}