
package com.example.battleships.dtos

import com.example.battleships.game.domain.game.Game
import com.example.battleships.game.domain.game.GameState
import com.example.battleships.game.domain.game.Instants
import com.example.battleships.utils.hypermedia.SirenEntity
import pt.isel.daw.dawbattleshipgame.domain.board.Board
import com.example.battleships.game.domain.game.Configuration
import pt.isel.daw.dawbattleshipgame.domain.player.Player

data class CreateGameDtoProperties(val state: String, val gameId: Int?)
typealias CreateGameDto = SirenEntity<CreateGameDtoProperties>
val GameInfoDtoType = SirenEntity.getType<CreateGameDtoProperties>()

data class GameIdDtoProperties(val gameId: Int)
typealias GameIdDto = SirenEntity<GameIdDtoProperties>
val GameIdDtoType = SirenEntity.getType<GameIdDtoProperties>()

data class GameDtoProperties(
    val gameId: Int,
    val configuration: Configuration,
    val player1: Int,
    val player2: Int,
    val state: String,
    val board1: BoardDtoProperties,
    val board2: BoardDtoProperties,
    val playerTurn: Int?,
    val winner: Int?,
    val myPlayer: String
)
typealias GameDto = SirenEntity<GameDtoProperties>
val GameDtoType = SirenEntity.getType<GameDtoProperties>()

fun GameDto.toGameAndPlayer(): Pair<Game, Player> {
    val game = this.properties
    require(game != null) { "GameDto properties are null" }
    return Game(
        game.gameId,
        game.configuration,
        game.player1,
        game.player2,
        game.board1.toBoard(),
        game.board2.toBoard(),
        GameState.valueOf(game.state.uppercase()),
        instants = Instants(), // TODO -> review this
        game.playerTurn,
        winner = game.winner
    ) to Player.valueOf(game.myPlayer.uppercase())
}

data class BoardDtoProperties(
    val cells: String,
    val nCells: Int,
    val isConfirmed: Boolean
)

fun BoardDtoProperties.toBoard(): Board {
    return Board(cells, isConfirmed)
}

fun GameIdDto.toGameId(): Int {
    val properties = this.properties
    require(properties != null) { "GameActionDto properties are null" }
    return properties.gameId
}