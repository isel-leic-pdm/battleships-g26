package com.example.battleships.game

import com.example.battleships.game.domain.board.Coordinate
import com.example.battleships.game.domain.game.Configuration
import com.example.battleships.game.domain.game.Game
import com.example.battleships.game.domain.game.SinglePhase
import com.example.battleships.game.domain.game.single.PlayerPreparationPhase
import com.example.battleships.game.domain.ship.Orientation
import com.example.battleships.game.domain.ship.ShipType


interface BattleshipsService {
    fun startNewGame()

    fun placeShip(shipType: ShipType, coordinate: Coordinate, orientation: Orientation)

    fun moveShip(origin: Coordinate, destination: Coordinate)

    fun rotateShip(position: Coordinate)

    fun placeShot(coordinate: Coordinate)

    fun confirmFleet()

    fun getGameState(): Game?
}

class FakeBattleshipService : BattleshipsService {
    var game: Game? = null

    private val configuration = Configuration(
        boardSize = 10,
        fleet = setOf(
            Pair(ShipType.CARRIER, 5),
            Pair(ShipType.BATTLESHIP, 4),
            Pair(ShipType.CRUISER, 3),
            Pair(ShipType.SUBMARINE, 3),
            Pair(ShipType.DESTROYER, 2)
        ),
        nShotsPerRound = 10,
        roundTimeout = 10
    )

    override fun startNewGame() {
        val gameId = 111
        val player1Id = 222
        val player2Id = 333
        game = Game.newGame(gameId, player1Id, player2Id, configuration)
    }

    override fun placeShip(shipType: ShipType, coordinate: Coordinate, orientation: Orientation) {
        val localGame = game
        if (localGame is SinglePhase) {
            val playerGame = localGame.player1Game
            if (playerGame is PlayerPreparationPhase) {
                val newPlayerGame = playerGame.tryPlaceShip(shipType, coordinate, orientation)
                if (newPlayerGame != null) {
                    val newGame = localGame.copy(player1Game = newPlayerGame)
                    game = newGame
                }
            }
        }
    }

    override fun moveShip(origin: Coordinate, destination: Coordinate) {
        val localGame = game
        if (localGame is SinglePhase) {
            val playerGame = localGame.player1Game
            if (playerGame is PlayerPreparationPhase) {
                val newPlayerGame = playerGame.tryMoveShip(origin, destination)
                if (newPlayerGame != null) {
                    val newGame = localGame.copy(player1Game = newPlayerGame)
                    game = newGame
                }
            }
        }
    }

    override fun rotateShip(position: Coordinate) {
        val localGame = game
        if (localGame is SinglePhase) {
            val playerGame = localGame.player1Game
            if (playerGame is PlayerPreparationPhase) {
                val newPlayerGame = playerGame.tryRotateShip(position)
                if (newPlayerGame != null) {
                    val newGame = localGame.copy(player1Game = newPlayerGame)
                    game = newGame
                }
            }
        }
    }

    override fun placeShot(coordinate: Coordinate) {
        TODO("Not yet implemented")
    }

    override fun confirmFleet() {
        TODO("Not yet implemented")
    }

    override fun getGameState(): Game? {
        return game
    }
}