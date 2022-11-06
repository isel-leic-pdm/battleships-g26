package com.example.battleships.game.services

import com.example.battleships.game.domain.state.Game
import pt.isel.daw.dawbattleshipgame.domain.board.Coordinate
import pt.isel.daw.dawbattleshipgame.domain.ship.Orientation
import com.example.battleships.game.domain.ship.ShipType
import com.example.battleships.home.Home

/**
 * This interface is responsible for providing the options that interact with the game.
 */
interface BattleshipsService {
    suspend fun getHome(): Home

    suspend fun getRankings(): List<Ranking>

    suspend fun getServerInfo(): ServerInfo

    suspend fun getGameId(token: String): Int?

    suspend fun startNewGame(token: String)

    suspend fun placeShip(token: String, gameId: Int, shipType: ShipType, coordinate: Coordinate, orientation: Orientation)

    suspend fun moveShip(token: String, gameId: Int, origin: Coordinate, destination: Coordinate)

    suspend fun rotateShip(token: String, gameId: Int, position: Coordinate)

    suspend fun placeShot(token: String, gameId: Int, coordinate: Coordinate)

    suspend fun confirmFleet(token: String, gameId: Int)

    suspend fun getGame(token: String, gameId: Int): Game?
}