package com.example.battleships.game

import com.example.fleetbattletemp.game.domain.board.Coordinate
import com.example.fleetbattletemp.game.domain.game.Configuration
import com.example.fleetbattletemp.game.domain.game.Game
import com.example.fleetbattletemp.game.domain.ship.Orientation
import com.example.fleetbattletemp.game.domain.ship.ShipType


interface BattleshipsService {
    fun getNewGame(): Game?

    fun placeShip(
        game: Game,
        shipType: ShipType,
        coordinate: Coordinate,
        orientation: Orientation
    ): Game?

    fun moveShip(game: Game, origin: Coordinate, destination: Coordinate): Game?

    fun rotateShip(game: Game, position: Coordinate): Game?

    fun placeShot(game: Game, coordinate: Coordinate): Game?

    fun confirmFleet(game: Game): Game?
}

class FakeDataService : BattleshipsService {
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

    override fun getNewGame(): Game {
        return Game.newGame(configuration)
    }

    override fun placeShip(
        game: Game,
        shipType: ShipType,
        coordinate: Coordinate,
        orientation: Orientation
    ): Game? {
        return game.tryPlaceShip(shipType, coordinate, orientation)
    }

    override fun moveShip(game: Game, origin: Coordinate, destination: Coordinate): Game? {
        return game.tryMoveShip(origin, destination)
    }

    override fun rotateShip(game: Game, position: Coordinate): Game? {
        return game.tryRotateShip(position)
    }

    override fun placeShot(game: Game, coordinate: Coordinate): Game? {
        return game.tryPlaceShot(coordinate)
    }

    override fun confirmFleet(game: Game): Game? {
        return game.tryConfirmFleet()
    }
}