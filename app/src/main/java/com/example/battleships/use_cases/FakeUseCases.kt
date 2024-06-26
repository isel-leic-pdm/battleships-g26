package com.example.battleships.use_cases

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.battleships.dtos.HomeDto
import com.example.battleships.dtos.HomeDtoProperties
import com.example.battleships.game.domain.game.Configuration
import com.example.battleships.game.domain.game.Game
import com.example.battleships.game.domain.game.ShotsList
import com.example.battleships.home.Home
import com.example.battleships.home.UserHome
import com.example.battleships.info.ServerInfo
import com.example.battleships.rankings.UserRanking
import com.example.battleships.rankings.UserStats
import com.example.battleships.services.*
import com.example.battleships.services.fake.FakeGameDataServices
import com.example.battleships.services.fake.FakeHomeDataServices
import com.example.battleships.services.fake.FakeUserDataServices
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
    override suspend fun createGame(token: String, mode: Mode, configuration: Configuration?): Boolean {
        val gameId = gameServices.createGame(token, mode, configuration = configuration)
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
    override suspend fun fetchRankings(mode: Mode): UserRanking =
        homeServices.getRankings(mode)

    override suspend fun setFleet(
        token: String,
        ships: List<Triple<ShipType, com.example.battleships.game.domain.board.Coordinate, Orientation>>,
        mode: Mode
    ): Boolean {
        val result = gameServices.setFleet(token, ships, mode = mode)
        return getValueOrThrow(result)
    }

    override suspend fun placeShots(token: String, shots: ShotsList, mode: Mode): Boolean {
        val result = gameServices.placeShots(token, shots, mode = mode)
        return getValueOrThrow(result)
    }

    override suspend fun fetchServerInfo(mode: Mode): ServerInfo {
        return homeServices.getServerInfo(mode)
    }

    override suspend fun getUserById(id: Int, mode: Mode): UserStats {
        return homeServices.getUserById(id, mode)
    }

    override suspend fun getUserHome(token: String, mode: Mode): UserHome {
        return userServices.getUserHome(token, mode)
    }

    override suspend fun getHome() = Home(HomeDto(properties = HomeDtoProperties("fake")))

    override suspend fun checkIfUserIsInQueue(token: String, mode: Mode): Boolean {
        val result = gameServices.checkIfUserIsInQueue(token, null, mode)
        return getValueOrThrow(result)
    }

    override suspend fun surrender(token: String, gameId: Int, mode: Mode): Boolean {
        val res = gameServices.surrender(token, gameId, null, mode)
        return getValueOrThrow(res)
    }

}