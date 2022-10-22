package com.example.battleships.game.services

import com.example.battleships.game.domain.board.Coordinate
import com.example.battleships.game.domain.ship.Orientation
import com.example.battleships.game.domain.ship.ShipType
import com.example.battleships.game.domain.state.BattlePhase
import com.example.battleships.game.domain.state.Configuration
import com.example.battleships.game.domain.state.Game
import com.example.battleships.game.domain.state.SinglePhase
import com.example.battleships.game.domain.state.single.PlayerPreparationPhase
import com.example.battleships.game.domain.state.single.PlayerWaitingPhase

/**
 * This interface is responsible for providing the options that interact with the game.
 */
interface BattleshipsService {
    suspend fun getUserId(token: String): Int?

    suspend fun startNewGame(token: String)

    suspend fun placeShip(token: String, shipType: ShipType, coordinate: Coordinate, orientation: Orientation)

    suspend fun moveShip(token: String, origin: Coordinate, destination: Coordinate)

    suspend fun rotateShip(token: String, position: Coordinate)

    suspend fun placeShot(token: String, coordinate: Coordinate)

    suspend fun confirmFleet(token: String)

    suspend fun getGameState(token: String): Game?
}