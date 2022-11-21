package com.example.battleships.services.fake

import com.example.battleships.game.GameInfo
import com.example.battleships.game.domain.ship.ShipType
import com.example.battleships.game.domain.state.Configuration
import com.example.battleships.game.domain.state.Game
import com.example.battleships.services.GameDataServices
import com.example.battleships.services.Mode
import com.example.battleships.utils.hypermedia.SirenAction
import com.example.battleships.utils.hypermedia.SirenLink
import pt.isel.daw.dawbattleshipgame.domain.board.Coordinate

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
        roundTimeout = 10
    )

    override suspend fun createGame(
        token: String,
        mode: Mode,
        newCreateGameAction: SirenAction?
    ): GameInfo? {
        TODO("Not yet implemented")
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