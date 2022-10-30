package com.example.battleships.game

import com.example.battleships.game.domain.state.Game
import com.example.battleships.game.domain.state.GameState
import com.example.battleships.utils.hypermedia.SirenEntity
import pt.isel.daw.dawbattleshipgame.domain.board.Board
import pt.isel.daw.dawbattleshipgame.domain.state.Configuration

data class GameIdDtoProperties(val gameId: Int)

typealias GameIdDto = SirenEntity<GameIdDtoProperties>
val GameIdDtoType = SirenEntity.getType<GameIdDtoProperties>()

data class GameDtoProperties(
    val gameId: Int,
    val configuration: Configuration,
    val player1: Int,
    val player2: Int,
    val state: GameState,
    val board1: BoardDtoProperties,
    val board2: BoardDtoProperties,
)

typealias GameDto = SirenEntity<GameDtoProperties>
val GameDtoType = SirenEntity.getType<GameDtoProperties>()

fun GameDtoProperties.toGame() =
    Game(
        gameId,
        configuration,
        player1,
        player2,
        board1.toBoard(),
        board2.toBoard(),
        state
    )

data class BoardDtoProperties(
    val cells: Map<CoordinateDto, CoordinateContentDto>,
    val nCells: Int
)

data class CoordinateDto(
    val l: Int,
    val c: Int
)
data class CoordinateContentDto(val shipType: String, val isHit: Boolean)

typealias BoardDto = SirenEntity<BoardDtoProperties>
val BoardDtoType = SirenEntity.getType<BoardDtoProperties>()

fun BoardDtoProperties.toBoard(): Board {
    TODO("Not yet implemented")
}