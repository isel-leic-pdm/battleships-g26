package com.example.fleetbattletemp.game.domain.game.game_state

import com.example.fleetbattletemp.game.domain.board.Board
import com.example.fleetbattletemp.game.domain.game.Configuration

class Waiting(
    override val configuration: Configuration,
    override val myBoard: Board,
) : GameState()