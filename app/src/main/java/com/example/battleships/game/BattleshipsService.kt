package com.example.battleships.game

import androidx.compose.runtime.rememberCoroutineScope
import com.example.battleships.game.domain.board.Coordinate
import com.example.battleships.game.domain.state.Configuration
import com.example.battleships.game.domain.state.Game
import com.example.battleships.game.domain.state.SinglePhase
import com.example.battleships.game.domain.state.single.PlayerPreparationPhase
import com.example.battleships.game.domain.ship.Orientation
import com.example.battleships.game.domain.ship.ShipType
import com.example.battleships.game.domain.state.BattlePhase
import com.example.battleships.game.domain.state.single.PlayerWaitingPhase
import kotlinx.coroutines.CoroutineScope


interface BattleshipsService {
    suspend fun startNewGame()

    suspend fun placeShip(shipType: ShipType, coordinate: Coordinate, orientation: Orientation)

    suspend fun moveShip(origin: Coordinate, destination: Coordinate)

    suspend fun rotateShip(position: Coordinate)

    suspend fun placeShot(coordinate: Coordinate)

    suspend fun confirmFleet()

    suspend fun getGameState(): Game?
    suspend fun letOpponentPlaceShotOnMe()
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

    override suspend fun startNewGame() {
        val gameId = 555
        val player1Id = 111
        val player2Id = 222
        game = Game.newGame(gameId, player1Id, player2Id, configuration)
        placeOpponentShips()
    }

    private suspend fun placeOpponentShips() {
        val localGame = game ?: return
        if (localGame is SinglePhase) {
            val opponentGame = localGame.player2Game
            if (opponentGame is PlayerPreparationPhase) {
                val newOpponentGame = opponentGame.tryPlaceShip(ShipType.CARRIER, Coordinate(1, 1), Orientation.HORIZONTAL)
                    ?.tryPlaceShip(ShipType.BATTLESHIP, Coordinate(3, 1), Orientation.HORIZONTAL)
                    ?.tryPlaceShip(ShipType.CRUISER, Coordinate(5, 2), Orientation.HORIZONTAL)
                    ?.tryPlaceShip(ShipType.SUBMARINE, Coordinate(7, 3), Orientation.HORIZONTAL)
                    ?.tryPlaceShip(ShipType.DESTROYER, Coordinate(9, 4), Orientation.HORIZONTAL)
                    ?.confirmFleet() ?: throw IllegalStateException("Opponent fleet not placed")

                game = localGame.copy(player2Game = newOpponentGame)
            }
        }
    }

    override suspend fun placeShip(shipType: ShipType, coordinate: Coordinate, orientation: Orientation) {
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

    override suspend fun moveShip(origin: Coordinate, destination: Coordinate) {
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

    override suspend fun rotateShip(position: Coordinate) {
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

    /**
     * Places a shot in enemy fleet, in case is player 1 turn.
     * After that, it places a shot in player 1 fleet, just for test purposes.
     */
    override suspend fun placeShot(coordinate: Coordinate) {
        val localGame = game
        if (localGame is BattlePhase) {
            val newGame = localGame.tryPlaceShot(localGame.player1, coordinate)
            if (newGame != null)
                game = newGame
        }
    }

    override suspend fun letOpponentPlaceShotOnMe() {
        val localGame = game
        if (localGame is BattlePhase) {
            val randomCoordinate = Coordinate((1..configuration.boardSize).random(), (1..configuration.boardSize).random())
            val newGame = localGame.tryPlaceShot(localGame.player2, randomCoordinate)
            if (newGame != null)
                game = newGame
            else
                throw IllegalStateException("Opponent shot not placed")
        }
    }

    override suspend fun confirmFleet() {
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

    override suspend fun getGameState() = game
}