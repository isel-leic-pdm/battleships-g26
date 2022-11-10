package com.example.battleships.game.domain.state

import com.example.battleships.game.domain.player.Player
import com.example.battleships.game.domain.board.Board
import pt.isel.daw.dawbattleshipgame.domain.board.Coordinate
import pt.isel.daw.dawbattleshipgame.domain.board.CoordinateSet
import pt.isel.daw.dawbattleshipgame.domain.board.moveFromTo
import pt.isel.daw.dawbattleshipgame.domain.ship.Orientation
import com.example.battleships.game.domain.ship.ShipType
import pt.isel.daw.dawbattleshipgame.domain.ship.getOrientation
import pt.isel.daw.dawbattleshipgame.domain.ship.getShip

/**
 * Tries to place [shipType] on the Board, on give in [position].
 * @return updated Game or null, if is not possible to position [shipType] in [position]
 */
fun Game.onSquarePressed(
    shipType: ShipType,
    position: Coordinate,
    orientation: Orientation,
    player: Player = Player.ONE
): Game? {
    if (isShipPlaced(shipType, player)) return null
    return try {
        if (!this.configuration.isShipValid(shipType)) throw Exception("Invalid ship type")
        val shipCoordinates = generateShipCoordinates(shipType, position, orientation, player) ?: throw Exception()
        return when(player) {
            Player.ONE -> this.updateBoard(board1.placeShip(shipCoordinates, shipType), Player.ONE)
            Player.TWO -> this.updateBoard(board2.placeShip(shipCoordinates, shipType), Player.TWO)
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
    player : Player = Player.ONE
) : Game? {
    return try {
        if (!this.configuration.isShipValid(shipType)) throw Exception("Invalid ship type")
        return this.updateBoard(getBoard(player).placeShip(coordinates, shipType), player)
    }catch (e : Exception){
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
    } catch (e : Exception){
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
        if(!getBoard(player).isShip(p)) throw Exception()
        val ship = getBoard(player).getShips().getShip(p)
        val shipCoordinates = ship.coordinates
        return this.updateBoard(getBoard(player).placeWaterPanel(shipCoordinates), player)
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
        return tmpGame?.onSquarePressed(ship.type, shipPosOrigin, curOrientation.other(), player)
    }catch (e : Exception){
        null
    }
}


/**
 * Builds a new Game object, with place shot on [shot], in opponent board.
 * If this shot sinks all enemy fleet, the game is over. In this case, End object is returned.
 */
fun Game.placeShot(userId: Int, shot: Coordinate, player: Player = Player.ONE): Game? {
    return try {
        val gameResult = this.updateBoard(getBoard(player).placeShot(shot), player).switchTurn()
        if (gameResult.board1.allShipsSunk() || gameResult.board2.allShipsSunk()) {
            Game(gameId, configuration, player1, player2, board1, board2, GameState.FINISHED, winner = userId)
        } else {
            gameResult
        }
    } catch (e: Exception) {
        null
    }
}


fun Game.confirmFleet(player: Player) =
    this.updateBoard(getBoard(player).confirm(), player, GameState.BATTLE)

/** ------------------------------------------ Auxiliary functions ---------------------------------------**/

/**
 * @returns List of Coordinates with positions to build a ship or null if impossible
 */
private fun Game.generateShipCoordinates(
    ship: ShipType, position: Coordinate,
    orientation: Orientation, player: Player
): CoordinateSet? {
    if (getBoard(player).isShip(position)) return null

    val shipCoordinates = generateShipPanels(
        getShipLength(ship), position, orientation, player
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
 * Generates the coordinates needed to make the ship
 */
private fun Game.generateShipPanels(size: Int, coordinate: Coordinate, orientation: Orientation, player: Player): CoordinateSet? {
    var auxCoordinate = coordinate
    val set = mutableSetOf(coordinate)
    repeat(size - 1) {
        auxCoordinate = if (orientation === Orientation.HORIZONTAL)
            getBoard(player).coordinates.right(auxCoordinate) ?: return null
        else
            getBoard(player).coordinates.down(auxCoordinate) ?: return null
        set.add(auxCoordinate)
    }
    return set
}

/**
 * Retrieves the ship length according to class game configuration.
 */
private fun Game.getShipLength(shipType: ShipType) =
    configuration.fleet.first { it.first === shipType }.second

private fun Game.isShipPlaced(shipType: ShipType, player: Player) =
    getBoard(player).getShips().map { it.type }.any { it === shipType }



/**-------------------------------------------------------------------------------------------------------------------------------**/