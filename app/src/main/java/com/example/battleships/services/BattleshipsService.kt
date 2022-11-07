package com.example.battleships.services

import com.example.battleships.game.domain.state.Game
import pt.isel.daw.dawbattleshipgame.domain.board.Coordinate
import pt.isel.daw.dawbattleshipgame.domain.ship.Orientation
import com.example.battleships.game.domain.ship.ShipType
import com.example.battleships.home.Home
import com.example.battleships.info.ServerInfo
import com.example.battleships.rankings.GameRanking

/**
 * This interface is responsible for providing the options that interact with the game.
 */
interface BattleshipsService {
    /**
     * Used to identify how implementations SHOULD behave:
     * - [FORCE_REMOTE] is used to indicate that the operation MUST try to access
     * the remote data source
     * - [FORCE_LOCAL] is usd to indicate that the operation SHOULD only use the
     * the local version of the data, if available
     * - [AUTO] states that the selection of which data to use is left to the
     * implementation.
     */
    enum class Mode { FORCE_REMOTE, FORCE_LOCAL, AUTO }

    suspend fun getRankings(): GameRanking

    suspend fun getServerInfo(): ServerInfo

    fun createUser(username: String, password: String): Boolean

    suspend fun login(username: String, password: String): String?

    suspend fun getGameId(token: String): Int?

    suspend fun startNewGame(token: String)

    suspend fun placeShip(token: String, gameId: Int, shipType: ShipType, coordinate: Coordinate, orientation: Orientation)

    suspend fun moveShip(token: String, gameId: Int, origin: Coordinate, destination: Coordinate)

    suspend fun rotateShip(token: String, gameId: Int, position: Coordinate)

    suspend fun placeShot(token: String, gameId: Int, coordinate: Coordinate)

    suspend fun confirmFleet(token: String, gameId: Int)

    suspend fun getGame(token: String, gameId: Int): Game?
}