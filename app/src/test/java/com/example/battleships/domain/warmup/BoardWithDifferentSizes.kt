package com.example.battleships.domain.warmup

import com.example.battleships.game.domain.board.toCoordinate
import com.example.battleships.game.domain.game.Game
import com.example.battleships.game.domain.game.moveShip
import com.example.battleships.game.domain.game.placeShip
import com.example.battleships.utils.getGameTestConfiguration1
import com.example.battleships.utils.getGameTestConfiguration2
import com.example.battleships.utils.getGameTestConfiguration3
import com.example.battleships.utils.getGameTestConfiguration5
import org.junit.Assert.*
import org.junit.Test
import pt.isel.daw.dawbattleshipgame.domain.ship.Orientation
import pt.isel.daw.dawbattleshipgame.domain.ship.ShipType
import pt.isel.daw.dawbattleshipgame.utils.*


class BoardWithDifferentSizes {
    private val gameId = generateId()
    private val player1 = generateId()
    private val player2 = generateId()
    private var configuration = getGameTestConfiguration1()

    @Test
    fun config_10_size() {
        configuration = getGameTestConfiguration2()
        val game = Game.newGame(gameId, player1, player2, configuration)
        var gameResult = game.placeShip(ShipType.DESTROYER, "A1".toCoordinate(), Orientation.HORIZONTAL)
        gameResult = gameResult?.moveShip("A1".toCoordinate(), "B4".toCoordinate())
        assertEquals("    | A  | B  | C  | D  | E  | F  | G  | H  | I  | J  |\n" +
                "| 1 |    |    |    |    |    |    |    |    |    |    |\n" +
                "| 2 |    |    |    |    |    |    |    |    |    |    |\n" +
                "| 3 |    |    |    |    |    |    |    |    |    |    |\n" +
                "| 4 |    | [] | [] |    |    |    |    |    |    |    |\n" +
                "| 5 |    |    |    |    |    |    |    |    |    |    |\n" +
                "| 6 |    |    |    |    |    |    |    |    |    |    |\n" +
                "| 7 |    |    |    |    |    |    |    |    |    |    |\n" +
                "| 8 |    |    |    |    |    |    |    |    |    |    |\n" +
                "| 9 |    |    |    |    |    |    |    |    |    |    |\n" +
                "| 10|    |    |    |    |    |    |    |    |    |    |\n",
            gameResult?.getBoard().toString()
        )
    }


    @Test
    fun config_8_size() {
        configuration = getGameTestConfiguration3()
        val game = Game.newGame(gameId, player1, player2, configuration)
        var gameResult = game.placeShip(ShipType.DESTROYER, "A1".toCoordinate(), Orientation.HORIZONTAL)
        gameResult = gameResult?.moveShip("A1".toCoordinate(), "B4".toCoordinate())
        assertEquals("    | A  | B  | C  | D  | E  | F  | G  | H  |\n" +
                "| 1 |    |    |    |    |    |    |    |    |\n" +
                "| 2 |    |    |    |    |    |    |    |    |\n" +
                "| 3 |    |    |    |    |    |    |    |    |\n" +
                "| 4 |    | [] | [] |    |    |    |    |    |\n" +
                "| 5 |    |    |    |    |    |    |    |    |\n" +
                "| 6 |    |    |    |    |    |    |    |    |\n" +
                "| 7 |    |    |    |    |    |    |    |    |\n" +
                "| 8 |    |    |    |    |    |    |    |    |\n",
            gameResult?.getBoard().toString()
        )
    }

    @Test
    fun config_13_size() {
        configuration = getGameTestConfiguration5()
        val game = Game.newGame(gameId, player1, player2, configuration)
        var gameResult = game.placeShip(ShipType.DESTROYER, "A1".toCoordinate(), Orientation.HORIZONTAL)
        gameResult = gameResult?.moveShip("A1".toCoordinate(), "B4".toCoordinate())
        assertEquals("    | A  | B  | C  | D  | E  | F  | G  | H  | I  | J  | K  | L  | M  |\n" +
                "| 1 |    |    |    |    |    |    |    |    |    |    |    |    |    |\n" +
                "| 2 |    |    |    |    |    |    |    |    |    |    |    |    |    |\n" +
                "| 3 |    |    |    |    |    |    |    |    |    |    |    |    |    |\n" +
                "| 4 |    | [] | [] |    |    |    |    |    |    |    |    |    |    |\n" +
                "| 5 |    |    |    |    |    |    |    |    |    |    |    |    |    |\n" +
                "| 6 |    |    |    |    |    |    |    |    |    |    |    |    |    |\n" +
                "| 7 |    |    |    |    |    |    |    |    |    |    |    |    |    |\n" +
                "| 8 |    |    |    |    |    |    |    |    |    |    |    |    |    |\n" +
                "| 9 |    |    |    |    |    |    |    |    |    |    |    |    |    |\n" +
                "| 10|    |    |    |    |    |    |    |    |    |    |    |    |    |\n" +
                "| 11|    |    |    |    |    |    |    |    |    |    |    |    |    |\n" +
                "| 12|    |    |    |    |    |    |    |    |    |    |    |    |    |\n" +
                "| 13|    |    |    |    |    |    |    |    |    |    |    |    |    |\n",
            gameResult?.getBoard().toString()
        )
    }

}