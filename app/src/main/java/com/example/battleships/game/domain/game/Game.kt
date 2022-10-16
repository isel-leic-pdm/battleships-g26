package com.example.fleetbattletemp.game.domain.game

import com.example.fleetbattletemp.game.domain.board.Board
import com.example.fleetbattletemp.game.domain.board.Coordinate
import com.example.fleetbattletemp.game.domain.board.Coordinates
import com.example.fleetbattletemp.game.domain.board.toCoordinate
import com.example.fleetbattletemp.game.domain.game.game_state.*
import com.example.fleetbattletemp.game.domain.player.Player
import com.example.fleetbattletemp.game.domain.ship.Orientation
import com.example.fleetbattletemp.game.domain.ship.ShipType

enum class State { WARMUP, WAITING, BATTLE, END }

/**
 * Represents the main Battleship game model.
 * This is the only class that should be used to interact with the game.
 * All the state is immutable, so every time a change is made, a new instance of the game is created.
 * The state is also controlled by the class, so it is not possible to create an invalid state.
 * There are three states: WARMUP, BATTLE and END.
 * Through this class, depending on the calls, the state can change, and, so, the same calls can become invalid.
 * For example, after confirming the fleet, the state has changed to battle, and, it is not possible to place a new ship.
 */
class Game {
    private val gameState: GameState
    val configuration: Configuration
        get() = gameState.configuration
    private val player: Player
    val state: State
        get() = getGameState()
    val myBoard: Board
    val boardCoordinates: List<Coordinate>
        get() = myBoard.coordinates
    val opponentBoard: Board?
        get() = getOpponentBoardInternal()

    companion object {
        fun newGame(configuration: Configuration, player: Player = Player.Player1) =
            Game(Warmup(configuration), player)
    }

    private constructor(newGameState: GameState, player: Player) {
        gameState = newGameState
        this.player = player
        myBoard = newGameState.myBoard
    }

    override fun toString() = gameState.toString()

    operator fun get(coordinate: Coordinate) = myBoard[coordinate]

    private fun getGameState(): State {
        return when (gameState) {
            is Warmup -> State.WARMUP
            is Battle -> State.BATTLE
            is Waiting -> State.WAITING
            is End -> State.END
        }
    }

    private fun getOpponentBoardInternal(): Board? {
        return when (gameState) {
            is Battle -> gameState.opponentBoard
            is End -> gameState.opponentBoard
            else -> null
        }
    }

    fun tryPlaceShip(
        ship: ShipType,
        position: Coordinate,
        orientation: Orientation
    ): Game? {
        return if (gameState is Warmup) {
            val gameResult = gameState.tryPlaceShip(ship, position, orientation) ?: return null
            Game(gameResult, player)
        } else null
    }

    /**
     * Places a ship on the board.
     * @param command the command to place the ship. Format: "shipType,coordinate,orientation"
     */
    fun tryPlaceShip(command: String): Game? {
        return if (gameState is Warmup) {
            val parsedCommand = parseCommand(command) ?: return null
            val gameResult = gameState.tryPlaceShip(
                parsedCommand.first,
                parsedCommand.second,
                parsedCommand.third
            ) ?: return null
            Game(gameResult, player)
        } else null
    }

    /**
     * Parses a string command to place a ship.
     * @param command the command to place the ship. Format: "shipType,coordinate,orientation"
     * @return a triple with the ship type, the coordinate and the orientation
     */
    private fun parseCommand(command: String): Triple<ShipType, Coordinate, Orientation>? {
        val commandParts = command.split(",")
        if (commandParts.size != 3) return null
        val shipType = ShipType.valueOf(commandParts[0])
        val coordinate = commandParts[1].toCoordinate() ?: return null
        val orientation = Orientation.valueOf(commandParts[2])
        return Triple(shipType, coordinate, orientation)
    }

    fun tryMoveShip(position: Coordinate, destination: Coordinate): Game? {
        if (gameState is Warmup) {
            val newGameResult = gameState.tryMoveShip(position, destination) ?: return null
            return Game(newGameResult, player)
        } else return null
    }

    fun tryPlaceShot(c: Coordinate): Game? {
        if (gameState is Battle) {
            val newGameResult = gameState.tryPlaceShot(c) ?: return null
            return Game(newGameResult, player)
        } else return null
    }

    /**
     * Builds a new Game object, with the fleet confirmed.
     * This function will result in a new Game object, with the state changed to BATTLE.
     * From this point, it is not possible to place/move/rotate new ships.
     */
    fun tryConfirmFleet(): Game? {
        return if (gameState is Warmup) {
            Game(Waiting(gameState.configuration, gameState.myBoard), player)
        } else null
    }

    fun tryRotateShip(position: Coordinate): Game? {
        return if (gameState is Warmup) {
            val gameResult = gameState.tryRotateShip(position) ?: return null
            Game(gameResult, player)
        } else null
    }

    fun tryRotateShip(position: String): Game? {
        return if (gameState is Warmup) {
            val coordinate = position.toCoordinate() ?: return null
            val gameResult = gameState.tryRotateShip(coordinate) ?: return null
            Game(gameResult, player)
        } else null
    }

    fun isShip(it: Coordinate): Boolean {
        return gameState.myBoard.isShipPanel(it)
    }

    fun generateShips(): Game? {
        var game = this
        var auxGame: Game? = this
        if (game.gameState is Warmup) {
            ShipType.values().forEach {
                do {
                    auxGame = game.tryPlaceShip(
                        it,
                        Coordinates(game.gameState.configuration.boardSize).random(),
                        Orientation.random(),
                    )
                } while (auxGame == null)
                game = auxGame!!
                auxGame = null
            }
            return game
        }
        return null
    }
}
