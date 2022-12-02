package com.example.battleships.services

import com.example.battleships.game.domain.game.Game
import com.example.battleships.utils.hypermedia.SirenAction
import com.example.battleships.utils.hypermedia.SirenLink
import pt.isel.daw.dawbattleshipgame.domain.board.Coordinate
import pt.isel.daw.dawbattleshipgame.domain.player.Player
import pt.isel.daw.dawbattleshipgame.domain.ship.Orientation
import pt.isel.daw.dawbattleshipgame.domain.ship.ShipType

/**
 * This interface is responsible for providing the options that interact with the game.
 */
interface GameDataServices {
    suspend fun createGame(token: String, mode: Mode, newCreateGameAction: SirenAction? = null): Boolean

    suspend fun getCurrentGameId(token: String, mode: Mode, newGetCurrentGameIdLink: SirenLink? = null): Int?

    /**
     * Sets a fleet of ships for the player.
     * @note after calling this function, its not possible to change the fleet.
     */
    suspend fun setFleet(
        token: String,
        ships: List<Triple<ShipType, Coordinate, Orientation>>,
        newSetFleetAction: SirenAction? = null,
        mode: Mode
    ): Boolean

    suspend fun placeShot(token: String, coordinate: Coordinate, newPlaceShotAction: SirenAction?, mode: Mode): Boolean

    /**
     * This function is responsible for getting the game state.
     * @return an object representing the current game state, and the player associated with the token.
     */
    suspend fun getGame(token: String, newGetGameLink: SirenLink? = null, mode: Mode): Pair<Game, Player>?
}