package com.example.battleships.domain.warmup

import com.example.battleships.game.domain.board.toCoordinate
import com.example.battleships.game.domain.game.Game
import com.example.battleships.game.domain.game.placeShip
import com.example.battleships.game.domain.game.rotateShip
import org.junit.Assert.*
import org.junit.Test
import pt.isel.daw.dawbattleshipgame.domain.ship.Orientation
import pt.isel.daw.dawbattleshipgame.domain.ship.ShipType
import pt.isel.daw.dawbattleshipgame.utils.generateId
import com.example.battleships.utils.getGameTestConfiguration4


class GameTestRotation {
    private val gameId = generateId()
    private val player1 = generateId()
    private val player2 = generateId()
    private val configuration = getGameTestConfiguration4()

    @Test
    fun rotating_ship_on_valid_location_1() {
        val game1 = Game.newGame(gameId, player1, player2, configuration)
        val game2 = game1.placeShip(ShipType.DESTROYER, "C2".toCoordinate(), Orientation.HORIZONTAL)
        val game3 = game2?.rotateShip("c2".toCoordinate())
        assertEquals(
            "    | A  | B  | C  | D  | E  | F  | G  | H  | I  | J  |\n" +
                    "| 1 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 2 |    |    | [] |    |    |    |    |    |    |    |\n" +
                    "| 3 |    |    | [] |    |    |    |    |    |    |    |\n" +
                    "| 4 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 5 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 6 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 7 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 8 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 9 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 10|    |    |    |    |    |    |    |    |    |    |\n",
            game3.toString()
        )
    }
    @Test
    fun rotating_ship_on_valid_location_2() {
        val game1 = Game.newGame(gameId, player1, player2, configuration) 
        val game2 = game1.placeShip(ShipType.DESTROYER, "C2".toCoordinate(), Orientation.HORIZONTAL)
        val game3 = game2?.rotateShip("d2".toCoordinate())
        assertEquals(
            "    | A  | B  | C  | D  | E  | F  | G  | H  | I  | J  |\n" +
                    "| 1 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 2 |    |    | [] |    |    |    |    |    |    |    |\n" +
                    "| 3 |    |    | [] |    |    |    |    |    |    |    |\n" +
                    "| 4 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 5 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 6 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 7 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 8 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 9 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 10|    |    |    |    |    |    |    |    |    |    |\n",
            game3.toString()
        )
    }

    @Test
    fun rotating_empty_coordinate_1() {
        val game1 = Game.newGame(gameId, player1, player2, configuration) 
        val game2 = game1.placeShip(ShipType.DESTROYER, "C2".toCoordinate(), Orientation.HORIZONTAL)
        val game3 = game2?.rotateShip("c3".toCoordinate())
        assertNull(game3)
    }

    @Test
    fun rotating_empty_coordinate_2() {
        val game1 = Game.newGame(gameId, player1, player2, configuration) 
        val game2 = game1.placeShip(ShipType.DESTROYER, "C2".toCoordinate(), Orientation.HORIZONTAL)
        val game3 = game2?.rotateShip("d3".toCoordinate())
        assertNull(game3)
    }

    @Test
    fun rotating_empty_coordinate_3() {
        val game1 = Game.newGame(gameId, player1, player2, configuration) 
        val game2 = game1.placeShip(ShipType.DESTROYER, "C2".toCoordinate(), Orientation.HORIZONTAL)
        val game3 = game2?.rotateShip("b2".toCoordinate())
        assertNull(game3)
    }

    @Test
    fun rotating_empty_coordinate_4() {
        val game1 = Game.newGame(gameId, player1, player2, configuration) 
        val game2 = game1.placeShip(ShipType.DESTROYER, "C2".toCoordinate(), Orientation.HORIZONTAL)
        val game3 = game2?.rotateShip("c1".toCoordinate())
        assertNull(game3)
    }

    @Test
    fun rotating_ship_and_colliding_with_another_1() {
        val game1 = Game.newGame(gameId, player1, player2, configuration) 
        val game2 = game1.placeShip(ShipType.CARRIER, "C2".toCoordinate(), Orientation.HORIZONTAL)
        val game3 = game2?.placeShip(ShipType.DESTROYER, "C4".toCoordinate(), Orientation.HORIZONTAL)
        val game4 = game3?.rotateShip("c2".toCoordinate())
        assertNull(game4)
    }

    // TODO -> see this tests
    fun rotating_ship_and_colliding_with_another_2() {
        /*
        val game1 = Game.newGame(gameId, player1, player2, configuration).player1PreparationPhase
        val game2 = game1.placeShip(ShipType.DESTROYER, "C2".toCoordinate(), Orientation.HORIZONTAL)
        val game3 = game2?.placeShip("c4")
        val game4 = game3?.rotateShip("d2".toCoordinate())
        assertNull(game4)
         */
    }

    @Test
    fun rotating_ship_and_not_colliding_with_another_1() {
        val game1 = Game.newGame(gameId, player1, player2, configuration) 
        val game2 = game1.placeShip(ShipType.DESTROYER, "c2".toCoordinate(), Orientation.HORIZONTAL)
        val game3 = game2?.placeShip(ShipType.CARRIER, "c5".toCoordinate(), Orientation.HORIZONTAL)
        val game4 = game3?.rotateShip("c2".toCoordinate())
        assertEquals(
            "    | A  | B  | C  | D  | E  | F  | G  | H  | I  | J  |\n" +
                    "| 1 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 2 |    |    | [] |    |    |    |    |    |    |    |\n" +
                    "| 3 |    |    | [] |    |    |    |    |    |    |    |\n" +
                    "| 4 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 5 |    |    | [] | [] | [] | [] | [] |    |    |    |\n" +
                    "| 6 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 7 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 8 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 9 |    |    |    |    |    |    |    |    |    |    |\n" +
                    "| 10|    |    |    |    |    |    |    |    |    |    |\n",
            game4.toString()
        )
    }

    @Test
    fun rotating_ship_and_colliding_with_wall() {
        val game1 = Game.newGame(gameId, player1, player2, configuration) 
            val game2 = game1.placeShip(ShipType.DESTROYER, "A10".toCoordinate(), Orientation.HORIZONTAL)
            val game3 = game2?.rotateShip("A10".toCoordinate())
            assertNull(game3)
        }


}