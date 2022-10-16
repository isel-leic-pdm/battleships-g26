package com.example.battleships.game

import com.example.battleships.game.domain.board.Coordinate
import com.example.battleships.game.domain.state.Configuration
import com.example.battleships.game.domain.state.Game
import com.example.battleships.game.domain.state.SinglePhase
import com.example.battleships.game.domain.state.single.PlayerPreparationPhase
import com.example.battleships.game.domain.ship.Orientation
import com.example.battleships.game.domain.ship.ShipType
import com.example.battleships.game.domain.state.BattlePhase
import com.example.battleships.game.domain.state.single.PlayerWaitingPhase


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
        placeOpponentShips()
    }

    private fun placeOpponentShips() {
        val localGame = game ?: return
        if (localGame is SinglePhase) {
            val opponentGame = localGame.player2Game
            if (opponentGame is PlayerPreparationPhase) {
                opponentGame.tryPlaceShip(ShipType.CARRIER, Coordinate(1, 1), Orientation.HORIZONTAL)
                opponentGame.tryPlaceShip(ShipType.BATTLESHIP, Coordinate(3, 1), Orientation.HORIZONTAL)
                opponentGame.tryPlaceShip(ShipType.CRUISER, Coordinate(5, 2), Orientation.HORIZONTAL)
                opponentGame.tryPlaceShip(ShipType.SUBMARINE, Coordinate(7, 3), Orientation.HORIZONTAL)
                opponentGame.tryPlaceShip(ShipType.DESTROYER, Coordinate(9, 4), Orientation.HORIZONTAL)
                val newOpponentGame = opponentGame.confirmFleet()
                game = localGame.copy(player2Game = newOpponentGame)
            }
        }
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
        val localGame = game
        if (localGame is SinglePhase) {
            val playerGame = localGame.player1Game
            val opponentGame = localGame.player2Game
            if (playerGame is PlayerPreparationPhase) {
                val newPlayerGame = playerGame.confirmFleet()
                game = if (opponentGame is PlayerWaitingPhase) {
                    BattlePhase(
                        configuration,
                        localGame.gameId,
                        localGame.player1,
                        localGame.player2,
                        localGame.player1Game.board,
                        localGame.player2Game.board
                    )
                } else
                    localGame.copy(player1Game = newPlayerGame)
            }
        }
    }

    override fun getGameState(): Game? {
        return game
    }
}