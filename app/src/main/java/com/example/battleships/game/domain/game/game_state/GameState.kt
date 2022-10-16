package com.example.fleetbattletemp.game.domain.game.game_state

import com.example.fleetbattletemp.game.domain.board.Board
import com.example.fleetbattletemp.game.domain.game.Configuration

sealed class GameState {
    abstract val configuration: Configuration
    abstract val myBoard: Board
}