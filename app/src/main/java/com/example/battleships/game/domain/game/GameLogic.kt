package com.example.battleships.game.domain.game

import com.example.battleships.game.domain.ship.getOrientation
import com.example.battleships.game.domain.ship.getShip
import pt.isel.daw.dawbattleshipgame.domain.board.Board
import pt.isel.daw.dawbattleshipgame.domain.board.Coordinate
import pt.isel.daw.dawbattleshipgame.domain.board.CoordinateSet
import pt.isel.daw.dawbattleshipgame.domain.board.moveFromTo
import pt.isel.daw.dawbattleshipgame.domain.player.Player
import pt.isel.daw.dawbattleshipgame.domain.ship.Orientation
import pt.isel.daw.dawbattleshipgame.domain.ship.ShipType

/**
 * Tries to place [shipType] on the Board, on give in [position].
 * @return updated Game or null, if is not possible to position [shipType] in [position]
 */
fun Game.placeShip(
        shipType: ShipType,
        position: Coordinate,
        orientation: Orientation,
        player: Player = Player.ONE
): Game? {
    if (isShipPlaced(shipType, player)) return null
    return try {
        if (!this.configuration.isShipValid(shipType)) throw Exception("Invalid ship type")
        val shipCoordinates = generateShipCoordinates(shipType, position, orientation, player) ?: throw Exception()
        return when (player) {
            Player.ONE -> this.updateGame(board1.placeShip(shipCoordinates, shipType), Player.ONE, null)
            Player.TWO -> this.updateGame(board2.placeShip(shipCoordinates, shipType), Player.TWO, null)
        }
    } catch (e: Exception) {
        null
    }
}

/**
 * Tries to place a ship, giving its coordinates and its type
 */
private fun Game.placeShip(
        shipType: ShipType,
        coordinates: CoordinateSet,
        player: Player = Player.ONE
): Game? {
    return try {
        if (!this.configuration.isShipValid(shipType)) throw Exception("Invalid ship type")
        if (isShipTouchingAnother(player, coordinates, shipType)) throw Exception("Invalid location")
        return this.updateGame(getBoard(player).placeShip(coordinates, shipType), player, null)
    } catch (e: Exception) {
        null
    }
}

/**
 * Generates a new Warmup Board with a moved ship
 */
fun Game.moveShip(
        position: Coordinate,
        destination: Coordinate,
        player: Player = Player.ONE,
): Game? {
    return try {
        val ship = getBoard(player).getShips().getShip(position)
        val newCoordinates = ship.coordinates.moveFromTo(position, destination, this.configuration.boardSize)
        val newGame = this.removeShip(position, player)?.placeShip(ship.type, newCoordinates, player) ?: return null
        if (isShipTouchingAnother(player, newCoordinates, ship.type)) return null
        else newGame
    } catch (e: Exception) {
        null
    }
}

/**
 * Tries to remove ship from the game, if one exists.
 * @param p coordinate where the ship is located (some part of the ship)
 * @return new Game with ship removed or null if ship was not found, for [p]
 */
private fun Game.removeShip(p: Coordinate, player: Player = Player.ONE): Game? {
    return try {
        if (!getBoard(player).isShip(p)) throw Exception()
        val ship = getBoard(player).getShips().getShip(p)
        val shipCoordinates = ship.coordinates
        return this.updateGame(getBoard(player).placeWaterPanel(shipCoordinates), player, null)
    } catch (e: Exception) {
        null
    }
}

/**
 * Tries to rotate a ship, if possible.
 * @return newly created game, with ship rotated, or null if not possible
 */
fun Game.rotateShip(position: Coordinate, player: Player = Player.ONE): Game? {
    return try {
        val ship = getBoard(player).getShips().getShip(position)
        val curOrientation = ship.getOrientation()
        val shipPosOrigin = getBoard(player).getShips().getShip(position).coordinates.first()
        val tmpGame = removeShip(position, player)
        return tmpGame?.placeShip(ship.type, shipPosOrigin, curOrientation.other(), player)
    } catch (e: Exception) {
        null
    }
}


fun Game.confirmFleet(player: Player): Game? {
    if (!getBoard(player).allShipsPlaced(configuration.fleet.toMap())) return null
    val isOtherConfirmed = getBoard(player.other()).isConfirmed()
    return this.updateGame(
            getBoard(player).confirm(),
            player,
            if (isOtherConfirmed) this.player1 else null, // player 1 always starts first
            if (isOtherConfirmed) GameState.BATTLE else GameState.FLEET_SETUP
    )
}

/**
 * Builds a new Game object, with place shot on [shot], in opponent board.
 * If this shot sinks all enemy fleet, the game is over. In this case, End object is returned.
 */
fun Game.placeShot(userId: Int, shot: Coordinate, player: Player): Game? {
    return try {
        val opponentBoard = getBoard(player.other())
        if (playerTurn != userId || opponentBoard.isHit(shot)) return null
        val gameResult =
                this.updateGame(opponentBoard.placeShot(shot), player.other(), getPlayerId(player.other()), GameState.BATTLE)
        if (gameResult.getBoard(player.other()).allShipsSunk()) {
            gameResult.setWinner(userId)
        } else {
            gameResult
        }
    } catch (e: Exception) {
        null
    }
}

/** ------------------------------------------ Auxiliary functions ---------------------------------------**/

/**
 * @returns List of Coordinates with positions to build a ship or null if impossible
 */
private fun Game.generateShipCoordinates(
        ship: ShipType, position: Coordinate,
        orientation: Orientation, player: Player
): CoordinateSet? {
    if (getBoard(player).isShip(position)) return null

    val shipCoordinates = getBoard(player).generateCoordinates(
            getShipLength(ship), position, orientation
    ) ?: return null

    if (isShipTouchingAnother(player, shipCoordinates, ship)) return null
    return shipCoordinates
}

/**
 * Detects if a Ship, given by [shipCoordinates] is touching another Ship (ShipPanel).
 */
private fun Game.isShipTouchingAnother(player: Player, shipCoordinates: CoordinateSet, shipType: ShipType): Boolean =
        shipCoordinates.any { isShipNearCoordinate(it, getBoard(player), shipType) }

/**
 * Check if any ship from the player is near the coordinate
 */
private fun isShipNearCoordinate(c: Coordinate, board: Board, shipType: ShipType) =
        board.coordinates.radius(c).any {
            board.isShip(it) && (board[it].shipType != shipType)
        }


/**
 * Retrieves the ship length according to class game configuration.
 */
private fun Game.getShipLength(shipType: ShipType) =
        configuration.fleet.first { it.first === shipType }.second

private fun Game.isShipPlaced(shipType: ShipType, player: Player) =
        getBoard(player).getShips().map { it.type }.any { it === shipType }


fun Game.generateShips(): Game {
    var game = this
    var auxGame: Game?

    //wrap function inside to fix repeated code
    fun tryRandomCoordinate(board: Board, size: Int, shipType: ShipType, player: Player) {
        do {
            val coordinates = board.generateCoordinates(size,
                    board.coordinates.random(), Orientation.random()
            )
            auxGame = if (coordinates != null) {
                game.placeShip(shipType, coordinates, player)
            } else null

        } while (auxGame == null)
        game = auxGame!!
        auxGame = null
    }

    configuration.fleet.forEach {
        tryRandomCoordinate(board1, it.second, it.first, Player.ONE)
        tryRandomCoordinate(board2, it.second, it.first, Player.TWO)
    }
    return game
}


/**-------------------------------------------------------------------------------------------------------------------------------**/