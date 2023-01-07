package com.example.battleships.services

import com.example.battleships.game.domain.game.Configuration
import com.example.battleships.game.domain.game.Game
import com.example.battleships.game.domain.game.ShotsList
import com.example.battleships.utils.hypermedia.SirenAction
import com.example.battleships.utils.hypermedia.SirenLink
import pt.isel.daw.dawbattleshipgame.domain.player.Player
import pt.isel.daw.dawbattleshipgame.domain.ship.Orientation
import pt.isel.daw.dawbattleshipgame.domain.ship.ShipType

/**
 * This interface is responsible for providing the options that interact with the game.
 */
interface GameDataServices {
    suspend fun createGame(token: String, mode: Mode,
                           CreateGameAction: SirenAction? = null,
                           configuration: Configuration? = null
    ): Either<ApiException, Boolean>

    suspend fun getCurrentGameId(token: String, GetCurrentGameIdLink: SirenLink? = null, mode: Mode): Either<ApiException, Int?>

    /**
     * Sets a fleet of ships for the player.
     * @note after calling this function, its not possible to change the fleet.
     */
    suspend fun setFleet(
        token: String,
        ships: List<Triple<ShipType, com.example.battleships.game.domain.board.Coordinate, Orientation>>,
        newSetFleetAction: SirenAction? = null,
        mode: Mode
    ): Either<ApiException, Boolean>

    suspend fun placeShots(token: String, shots: ShotsList, PlaceShotAction: SirenAction? = null, mode: Mode): Either<ApiException, Boolean>

    /**
     * This function is responsible for getting the game state.
     * @return an object representing the current game state, and the player associated with the token.
     */
    suspend fun getGame(token: String, GetGameLink: SirenLink? = null, mode: Mode): Either<ApiException, Pair<Game, Player>?>

    suspend fun checkIfUserIsInQueue(token: String, UserInQueueLink: SirenLink?, mode: Mode): Either<ApiException, Boolean>

    suspend fun surrender(token: String, SurrenderAction: SirenAction?, mode: Mode): Either<ApiException, Boolean>
}