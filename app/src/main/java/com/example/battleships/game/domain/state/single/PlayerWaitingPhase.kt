package com.example.battleships.game.domain.state.single

import com.example.battleships.game.domain.board.Board
import com.example.battleships.game.domain.state.Configuration

class PlayerWaitingPhase(val gameId: Int, val configuration: Configuration, override val board: Board, val playerId: Int): Single()