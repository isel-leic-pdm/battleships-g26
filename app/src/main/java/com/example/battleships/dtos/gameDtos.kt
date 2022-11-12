
package com.example.battleships.dtos

import com.example.battleships.game.GameInfo
import com.example.battleships.game.domain.state.Game
import com.example.battleships.game.domain.state.GameState
import com.example.battleships.utils.hypermedia.SirenEntity
import com.example.battleships.game.domain.board.Board
import com.example.battleships.game.domain.state.Configuration

data class GameInfoDtoProperties(val state: String, val gameId: Int?)
typealias GameInfoDto = SirenEntity<GameInfoDtoProperties>
val GameInfoDtoType = SirenEntity.getType<GameInfoDtoProperties>()

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

fun GameDto.toGame(): Game {
    val game = this.properties
    require(game != null) { "GameDto properties are null" }
    return Game(
        game.gameId,
        game.configuration,
        game.player1,
        game.player2,
        game.board1.toBoard(),
        game.board2.toBoard(),
        game.state
    )
}

data class BoardDtoProperties(
    val cells: String,
    val nCells: Int,
    val confirmed: Boolean
)

data class PlaceShipsDtoProperties(val shipIds: List<Int>)
typealias PlaceShipsDto = SirenEntity<PlaceShipsDtoProperties>
val PlaceShipsDtoType = SirenEntity.getType<PlaceShipsDtoProperties>()

fun BoardDtoProperties.toBoard(): Board {
    return Board(cells, confirmed)
}

fun GameInfoDto.toGameInfo(): GameInfo {
    val properties = this.properties
    require(properties != null) { "GameActionDto properties are null" }
    return GameInfo(properties.state, properties.gameId)
}

fun GameIdDto.toGameId(): Int {
    val properties = this.properties
    require(properties != null) { "GameActionDto properties are null" }
    return properties.gameId
}

fun PlaceShipsDto.toShipIds(): List<Int> {
    val properties = this.properties
    require(properties != null) { "GameActionDto properties are null" }
    return properties.shipIds
}