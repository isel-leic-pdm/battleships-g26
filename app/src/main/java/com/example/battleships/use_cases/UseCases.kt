package com.example.battleships.use_cases

import com.example.battleships.game.domain.game.Configuration
import com.example.battleships.game.domain.game.Game
import com.example.battleships.game.domain.game.ShotsList
import com.example.battleships.info.ServerInfo
import com.example.battleships.rankings.UserRanking
import com.example.battleships.rankings.UserStats
import com.example.battleships.services.Mode
import pt.isel.daw.dawbattleshipgame.domain.player.Player
import pt.isel.daw.dawbattleshipgame.domain.ship.Orientation
import pt.isel.daw.dawbattleshipgame.domain.ship.ShipType

interface UseCases{
    suspend fun createUser(username: String, password: String, mode: Mode = Mode.AUTO): Int

    suspend fun createToken(username: String, password: String, mode: Mode = Mode.AUTO): String

    suspend fun createGame(token: String, mode: Mode = Mode.AUTO, configuration : Configuration?): Boolean

    suspend fun fetchCurrentGameId(token: String, mode: Mode = Mode.AUTO): Int?

    suspend fun fetchGame(token: String, mode: Mode = Mode.AUTO): Pair<Game, Player>?

    suspend fun fetchRankings(mode: Mode = Mode.AUTO): UserRanking

    suspend fun setFleet(
        token: String,
        ships: List<Triple<ShipType, com.example.battleships.game.domain.board.Coordinate, Orientation>>,
        mode: Mode = Mode.AUTO
    ): Boolean

    suspend fun placeShots(token: String, shots: ShotsList, mode: Mode = Mode.AUTO): Boolean

    suspend fun fetchServerInfo(mode : Mode = Mode.AUTO) : ServerInfo

    suspend fun getUserById(id : Int, mode : Mode = Mode.AUTO) : UserStats
}