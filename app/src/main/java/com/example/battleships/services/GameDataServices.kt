package com.example.battleships.services

import com.example.battleships.game.GameInfo
import com.example.battleships.game.domain.ship.ShipType
import com.example.battleships.game.domain.state.Game
import com.example.battleships.utils.hypermedia.SirenAction
import com.example.battleships.utils.hypermedia.SirenLink
import pt.isel.daw.dawbattleshipgame.domain.board.Coordinate

/**
 * This interface is responsible for providing the options that interact with the game.
 */
interface GameDataServices {
    suspend fun createGame(token: String, mode: Mode, newCreateGameAction: SirenAction? = null): GameInfo?

    suspend fun getCurrentGameId(token: String, mode: Mode, newGetCurrentGameIdLink: SirenLink? = null): Int?

    suspend fun setFleet(
        token: String,
        ships: List<Pair<Coordinate, ShipType>>,
        mode: Mode,
        newSetFleetAction: SirenAction? = null,
        newConfirmFleetLayoutAction: SirenAction? = null
    ): Boolean

    suspend fun confirmFleetLayout(token: String, mode: Mode, newConfirmFleetLayoutAction: SirenAction? = null): Boolean?

    suspend fun placeShot(token: String, gameId: Int, coordinate: Coordinate, mode: Mode, newPlaceShotAction: SirenAction?): Boolean

    suspend fun getGame(token: String, gameId: Int, mode: Mode, newGetGameLink: SirenLink? = null): Game?
}