package com.example.battleships.services

import com.example.battleships.game.GameInfo
import com.example.battleships.game.domain.state.Game
import pt.isel.daw.dawbattleshipgame.domain.board.Coordinate
import pt.isel.daw.dawbattleshipgame.domain.ship.Orientation
import com.example.battleships.game.domain.ship.ShipType
import com.example.battleships.utils.hypermedia.SirenAction
import com.example.battleships.utils.hypermedia.SirenLink
import java.net.URL

/**
 * This interface is responsible for providing the options that interact with the game.
 */
interface GameDataServices {
    suspend fun createGame(token: String, mode: Mode, newCreateGameAction: SirenAction? = null): GameInfo?

    suspend fun getCurrentGameId(token: String, mode: Mode, newGetCurrentGameIdLink: SirenLink? = null): Int?

    suspend fun confirmLayout(token: String, gameId: Int, shipType: ShipType, coordinate: Coordinate, orientation: Orientation, mode: Mode): GameInfo?

    suspend fun placeShot(token: String, gameId: Int, coordinate: Coordinate, mode: Mode): GameInfo?

    suspend fun getGame(token: String, gameId: Int, mode: Mode): Game?
}