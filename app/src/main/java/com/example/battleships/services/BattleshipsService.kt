package com.example.battleships.services

import com.example.battleships.game.domain.state.Game
import pt.isel.daw.dawbattleshipgame.domain.board.Coordinate
import pt.isel.daw.dawbattleshipgame.domain.ship.Orientation
import com.example.battleships.game.domain.ship.ShipType
import com.example.battleships.home.Home
import com.example.battleships.info.ServerInfo
import com.example.battleships.rankings.GameRanking

// TODO -> split into, at least, three files: Home / Users / Games
/**
 * This interface is responsible for providing the options that interact with the game.
 */
interface BattleshipsService {
    suspend fun getRankings(): GameRanking

    suspend fun getServerInfo(): ServerInfo

    fun createUser(username: String, password: String): Boolean

    fun login(username: String, password: String): String?

    suspend fun getGameId(token: String): Int?

    suspend fun startNewGame(token: String)

    suspend fun placeShip(token: String, gameId: Int, shipType: ShipType, coordinate: Coordinate, orientation: Orientation)

    suspend fun moveShip(token: String, gameId: Int, origin: Coordinate, destination: Coordinate)

    suspend fun rotateShip(token: String, gameId: Int, position: Coordinate)

    suspend fun placeShot(token: String, gameId: Int, coordinate: Coordinate)

    suspend fun confirmFleet(token: String, gameId: Int)

    suspend fun getGame(token: String, gameId: Int): Game?
}