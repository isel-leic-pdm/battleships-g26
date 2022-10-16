package com.example.battleships.game.domain.state

import com.example.battleships.game.domain.board.Board

class EndPhase(
    override val gameId: Int,
    override val configuration: Configuration,
    override val player1: Int,
    override val player2: Int,
    val player1Board: Board,
    val player2Board: Board
    ) : Game()