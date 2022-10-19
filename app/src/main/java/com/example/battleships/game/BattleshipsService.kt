package com.example.battleships.game

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

    // suspend fun letOpponentPlaceShotOnMe(token: String)
}

class FakeBattleshipService : BattleshipsService {
    private val games = mutableMapOf<Pair<String, String>, Game>()

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

    override suspend fun getUserId(token: String): Int? {
        val (token1, token2, game) = getGameAndTokens(token) ?: return null
        return game.player1 // according to startGame, its the player is always player1
    }

    override suspend fun startNewGame(token: String) {
        val gameId = 555
        val player1Id = 111
        val player2Id = 222
        val opponentToken = "opponentToken"
        val game = Game.newGame(gameId, player1Id, player2Id, configuration)
        games[token to opponentToken] = game
        placeOpponentShips(opponentToken)
    }

    private suspend fun placeOpponentShips(token: String) {
        val (token1, token2, game) = getGameAndTokens(token) ?: return
        if (game is SinglePhase) {
            val opponentGame = game.player2Game
            if (opponentGame is PlayerPreparationPhase) {
                val newOpponentGame = opponentGame.tryPlaceShip(ShipType.CARRIER, Coordinate(1, 1), Orientation.HORIZONTAL)
                    ?.tryPlaceShip(ShipType.BATTLESHIP, Coordinate(3, 1), Orientation.HORIZONTAL)
                    ?.tryPlaceShip(ShipType.CRUISER, Coordinate(5, 2), Orientation.HORIZONTAL)
                    ?.tryPlaceShip(ShipType.SUBMARINE, Coordinate(7, 3), Orientation.HORIZONTAL)
                    ?.tryPlaceShip(ShipType.DESTROYER, Coordinate(9, 4), Orientation.HORIZONTAL)
                    ?.confirmFleet() ?: throw IllegalStateException("Opponent fleet not placed")

                games[token1 to token2] = game.copy(player2Game = newOpponentGame)
            }
        }
    }

    override suspend fun placeShip(token: String, shipType: ShipType, coordinate: Coordinate, orientation: Orientation) {
        val (token1, token2, game) = getGameAndTokens(token) ?: return
        if (game is SinglePhase) {
            val playerGame = game.player1Game
            if (playerGame is PlayerPreparationPhase) {
                val newPlayerGame = playerGame.tryPlaceShip(shipType, coordinate, orientation)
                if (newPlayerGame != null) {
                    val newGame = game.copy(player1Game = newPlayerGame)
                    games[token1 to token2] = newGame
                }
            }
        }
    }

    override suspend fun moveShip(token: String, origin: Coordinate, destination: Coordinate) {
        val (token1, token2, game) = getGameAndTokens(token) ?: return
        if (game is SinglePhase) {
            val playerGame = game.player1Game
            if (playerGame is PlayerPreparationPhase) {
                val newPlayerGame = playerGame.tryMoveShip(origin, destination)
                if (newPlayerGame != null) {
                    val newGame = game.copy(player1Game = newPlayerGame)
                    games[token1 to token2] = newGame
                }
            }
        }
    }

    override suspend fun rotateShip(token: String, position: Coordinate) {
        val (token1, token2, game) = getGameAndTokens(token) ?: return
        if (game is SinglePhase) {
            val playerGame = game.player1Game
            if (playerGame is PlayerPreparationPhase) {
                val newPlayerGame = playerGame.tryRotateShip(position)
                if (newPlayerGame != null) {
                    val newGame = game.copy(player1Game = newPlayerGame)
                    games[token1 to token2] = newGame
                }
            }
        }
    }

    /**
     * Places a shot in enemy fleet, in case is player 1 turn.
     * After that, it places a shot in player 1 fleet, just for test purposes.
     */
    override suspend fun placeShot(token: String, coordinate: Coordinate) {
        val (token1, token2, game) = getGameAndTokens(token) ?: return
        if (game is BattlePhase) {
            val newGame = game.tryPlaceShot(game.player1, coordinate)
            if (newGame != null)
                games[token1 to token2] = newGame
        }
        letOpponentPlaceShotOnMe(token2)
    }

    private suspend fun letOpponentPlaceShotOnMe(token: String) {
        val (token1, token2, game) = getGameAndTokens(token) ?: return
        if (game is BattlePhase) {
            val randomCoordinate = Coordinate((1..configuration.boardSize).random(), (1..configuration.boardSize).random())
            val newGame = game.tryPlaceShot(game.player2, randomCoordinate)
            if (newGame != null)
                games[token1 to token2] = newGame
            else
                throw IllegalStateException("Opponent shot not placed")
        }
    }

    override suspend fun confirmFleet(token: String) {
        val (token1, token2, game) = getGameAndTokens(token) ?: return
        if (game is SinglePhase) {
            val playerGame = game.player1Game
            val opponentGame = game.player2Game
            if (playerGame is PlayerPreparationPhase) {
                val newPlayerGame = playerGame.confirmFleet()
                val newGame = if (opponentGame is PlayerWaitingPhase) {
                    BattlePhase(
                        configuration,
                        game.gameId,
                        game.player1,
                        game.player2,
                        game.player1Game.board,
                        game.player2Game.board
                    )
                } else
                    game.copy(player1Game = newPlayerGame)
                games[token1 to token2] = newGame
            }
        }
    }

    override suspend fun getGameState(token: String) = getGameAndTokens(token)?.third

    private fun getGameAndTokens(token: String): Triple<String, String, Game>? {
        val (token1, token2) = games
            .filterKeys { it.first == token || it.second == token }
            .keys.firstOrNull() ?: return null
        val localGame = games[token1 to token2] ?: return null
        return Triple(token1, token2, localGame)
    }
}