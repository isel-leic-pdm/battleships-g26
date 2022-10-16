package com.example.battleships.game.domain.game.single

import com.example.battleships.game.domain.board.Board

sealed class Single {
    abstract val board: Board
}