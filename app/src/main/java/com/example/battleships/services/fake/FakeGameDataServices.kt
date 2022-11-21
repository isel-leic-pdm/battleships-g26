package com.example.battleships.services.fake

import com.example.battleships.services.GameDataServices
import com.example.battleships.services.Mode
import com.example.battleships.utils.hypermedia.SirenAction
import com.example.battleships.utils.hypermedia.SirenLink
import pt.isel.daw.dawbattleshipgame.domain.board.Board
import pt.isel.daw.dawbattleshipgame.domain.board.Coordinate
import pt.isel.daw.dawbattleshipgame.domain.game.Configuration
import pt.isel.daw.dawbattleshipgame.domain.game.Game
import pt.isel.daw.dawbattleshipgame.domain.game.GameState
import pt.isel.daw.dawbattleshipgame.domain.ship.ShipType

const val GAME_ID = 333
const val PLAYER1_ID = 1
const val PLAYER2_ID = 2
const val TIMEOUT = 10000L

class FakeGameDataServices : GameDataServices {
    private val configuration = Configuration(
        boardSize = 10,
        fleet = setOf(
            Pair(ShipType.CARRIER, 5),
            Pair(ShipType.BATTLESHIP, 4),
            Pair(ShipType.CRUISER, 3),
            Pair(ShipType.SUBMARINE, 3),
            Pair(ShipType.DESTROYER, 2)
        ),
        nShotsPerRound = 10,
        roundTimeout = TIMEOUT
    )

    var game: Game? = null

    override suspend fun createGame(
        token: String,
        mode: Mode,
        newCreateGameAction: SirenAction?
    ): Boolean {
        game = Game(
            GAME_ID,
            configuration,
            PLAYER1_ID,
            PLAYER2_ID,
            board1 = Board(configuration.boardSize),
            board2 = Board(configuration.boardSize),
            state = GameState.FLEET_SETUP,
            playerTurn = PLAYER1_ID
        )
        return true
    }

    override suspend fun getCurrentGameId(
        token: String,
        mode: Mode,
        newGetCurrentGameIdLink: SirenLink?
    ): Int? {
        TODO("Not yet implemented")
    }

    override suspend fun setFleet(
        token: String,
        ships: List<Pair<Coordinate, ShipType>>,
        mode: Mode,
        newSetFleetAction: SirenAction?,
        newConfirmFleetLayoutAction: SirenAction?
    ): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun confirmFleetLayout(
        token: String,
        mode: Mode,
        newConfirmFleetLayoutAction: SirenAction?
    ): Boolean? {
        TODO("Not yet implemented")
    }

    override suspend fun placeShot(
        token: String,
        gameId: Int,
        coordinate: Coordinate,
        mode: Mode,
        newPlaceShotAction: SirenAction?
    ): Boolean {
        TODO("Not yet implemented")
    }

    override suspend fun getGame(
        token: String,
        gameId: Int,
        mode: Mode,
        newGetGameLink: SirenLink?
    ): Game? {
        TODO("Not yet implemented")
    }

}