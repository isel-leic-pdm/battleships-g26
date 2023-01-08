package com.example.battleships.domain.warmup

import com.example.battleships.game.domain.board.toCoordinate
import com.example.battleships.game.domain.game.Game
import com.example.battleships.game.domain.game.placeShip
import org.junit.Assert.*
import org.junit.Test
import pt.isel.daw.dawbattleshipgame.domain.ship.Orientation
import pt.isel.daw.dawbattleshipgame.domain.ship.ShipType
import pt.isel.daw.dawbattleshipgame.utils.generateId
import com.example.battleships.utils.getGameTestConfiguration1


class GameTestPlacingSameShip {
    private val gameId = generateId()
    private val player1 = generateId()
    private val player2 = generateId()
    private val configuration = getGameTestConfiguration1()

    @Test
    fun `Placing destroyer ship in some location and then another destroyer, with same orientation, in different valid location`() {
        val game = Game.newGame(gameId, player1, player2, configuration)
        val gameResult1 = game.placeShip(ShipType.DESTROYER, "C2".toCoordinate(), Orientation.HORIZONTAL)
        val gameResult2 = gameResult1?.placeShip(ShipType.DESTROYER, "B9".toCoordinate(), Orientation.HORIZONTAL)
        assertNull(gameResult2)
    }

    @Test
    fun `Placing destroyer ship in some location and then another destroyer, with different orientation, in different valid location`() {
        val game = Game.newGame(gameId, player1, player2, configuration) 
        val gameResult1 = game.placeShip(ShipType.DESTROYER, "C2".toCoordinate(), Orientation.HORIZONTAL)
        val gameResult2 = gameResult1?.placeShip(ShipType.DESTROYER, "J1".toCoordinate(), Orientation.VERTICAL)
        assertNull(gameResult2)
    }

    @Test
    fun `Placing battleship ship, then Submarine and then battleship, in valid location`() {
        val game = Game.newGame(gameId, player1, player2, configuration) 
        val gameResult1 = game.placeShip(ShipType.BATTLESHIP, "C2".toCoordinate(), Orientation.HORIZONTAL)
        val gameResult2 = gameResult1?.placeShip(ShipType.SUBMARINE, "B4".toCoordinate(), Orientation.HORIZONTAL)
        val gameResult3 = gameResult2?.placeShip(ShipType.BATTLESHIP, "A8".toCoordinate(), Orientation.HORIZONTAL)
        assertNull(gameResult3)
    }

    @Test
    fun `Placing battleship ship, then Submarine and then battleship, upon previous one`() {
        val game = Game.newGame(gameId, player1, player2, configuration) 
        val gameResult1 = game.placeShip(ShipType.BATTLESHIP, "C2".toCoordinate(), Orientation.HORIZONTAL)
        val gameResult2 = gameResult1?.placeShip(ShipType.SUBMARINE, "B4".toCoordinate(), Orientation.HORIZONTAL)
        val gameResult3 = gameResult2?.placeShip(ShipType.BATTLESHIP, "C2".toCoordinate(), Orientation.HORIZONTAL)
        assertNull(gameResult3)
    }

    @Test
    fun `Placing battleship ship, then Submarine and then battleship, upon previous one, but different orientation`() {
        val game = Game.newGame(gameId, player1, player2, configuration) 
        val gameResult1 = game.placeShip(ShipType.BATTLESHIP, "C2".toCoordinate(), Orientation.HORIZONTAL)
        val gameResult2 = gameResult1?.placeShip(ShipType.SUBMARINE, "B4".toCoordinate(), Orientation.HORIZONTAL)
        val gameResult3 = gameResult2?.placeShip(ShipType.BATTLESHIP, "C2".toCoordinate(), Orientation.VERTICAL)
        assertNull(gameResult3)
    }

}