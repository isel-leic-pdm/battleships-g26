package com.example.battleships.use_cases

import com.example.battleships.game.domain.game.Configuration
import com.example.battleships.game.domain.game.Game
import com.example.battleships.info.ServerInfo
import com.example.battleships.rankings.GameRanking
import com.example.battleships.services.Mode
import pt.isel.daw.dawbattleshipgame.domain.board.Coordinate
import pt.isel.daw.dawbattleshipgame.domain.player.Player
import pt.isel.daw.dawbattleshipgame.domain.ship.Orientation
import pt.isel.daw.dawbattleshipgame.domain.ship.ShipType

interface UseCases{
    suspend fun createUser(username: String, password: String, mode: Mode = Mode.AUTO): Int

    suspend fun createToken(username: String, password: String, mode: Mode = Mode.AUTO): String

    suspend fun createGame(token: String, mode: Mode = Mode.AUTO, configuration : Configuration?): Boolean

    suspend fun fetchCurrentGameId(token: String, mode: Mode = Mode.AUTO): Int?

    suspend fun fetchGame(token: String, mode: Mode = Mode.AUTO): Pair<Game, Player>?

    suspend fun fetchRankings(mode: Mode = Mode.AUTO): GameRanking

    suspend fun setFleet(
        token: String,
        ships: List<Triple<ShipType, Coordinate, Orientation>>,
        mode: Mode = Mode.AUTO
    ): Boolean

    suspend fun placeShot(token: String, coordinate: Coordinate, mode: Mode = Mode.AUTO): Boolean

    suspend fun fetchServerInfo(mode : Mode = Mode.AUTO) : ServerInfo
}