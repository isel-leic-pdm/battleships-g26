
package com.example.battleships.dtos

import com.example.battleships.game.GameAction
import com.example.battleships.game.domain.state.Game
import com.example.battleships.game.domain.state.GameState
import com.example.battleships.utils.hypermedia.SirenEntity
import com.example.battleships.game.domain.board.Board
import com.example.battleships.game.domain.state.Configuration

data class GameActionDtoProperties(val state: String, val gameId: Int?)
typealias GameActionDto = SirenEntity<GameActionDtoProperties>
val GameActionDtoType = SirenEntity.getType<GameActionDtoProperties>()

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
    val cells: String,
    val nCells: Int,
    val confirmed: Boolean
)

data class CoordinateDto(
    val l: Int,
    val c: Int
)
data class CoordinateContentDto(val shipType: String, val isHit: Boolean)

typealias BoardDto = SirenEntity<BoardDtoProperties>
val BoardDtoType = SirenEntity.getType<BoardDtoProperties>()

fun BoardDtoProperties.toBoard(): Board {
    return Board(cells, confirmed)
}

fun GameActionDto.toGameAction(): GameAction {
    val properties = this.properties
    require(properties != null) { "GameActionDto properties are null" }
    return GameAction(properties.state, properties.gameId)
}