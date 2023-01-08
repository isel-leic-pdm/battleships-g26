package com.example.battleships.domain.warmup

import com.example.battleships.game.domain.game.Game
import com.example.battleships.game.domain.game.generateShips
import org.junit.Assert.*
import org.junit.Test
import pt.isel.daw.dawbattleshipgame.utils.generateId
import com.example.battleships.utils.getGameTestConfiguration1

internal class GameTestGenerateShips {

    @Test
    fun generateShips() {
        val gameId = generateId()
        val player1 = generateId()
        val player2 = generateId()
        val configuration = getGameTestConfiguration1()
        val game = Game.newGame(gameId, player1, player2, configuration)
        val g2 = game.generateShips()
        println("\n:Board 1:\n" + g2.board1)
        println("\n:Board 2:\n" + g2.board2)
        assertTrue(g2.allShipsPlaced())
    }
}