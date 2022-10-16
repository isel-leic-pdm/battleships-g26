package com.example.battleships.game.domain.state.single

import com.example.battleships.game.domain.board.Board

sealed class Single {
    abstract val board: Board
}