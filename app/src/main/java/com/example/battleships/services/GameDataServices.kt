package com.example.battleships.services

import com.example.battleships.game.domain.state.Game
import pt.isel.daw.dawbattleshipgame.domain.board.Coordinate
import pt.isel.daw.dawbattleshipgame.domain.ship.Orientation
import com.example.battleships.game.domain.ship.ShipType

/**
 * This interface is responsible for providing the options that interact with the game.
 */
interface GameDataServices {
    suspend fun startNewGame(token: String)

    suspend fun placeShip(token: String, gameId: Int, shipType: ShipType, coordinate: Coordinate, orientation: Orientation)

    suspend fun placeShot(token: String, gameId: Int, coordinate: Coordinate)

    suspend fun getGame(token: String, gameId: Int): Game?
}