package com.example.battleships.services.fake

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.battleships.game.ConfirmFleetButtonTestTag
import com.example.battleships.game.domain.game.*
import com.example.battleships.services.ApiException
import com.example.battleships.services.Either
import com.example.battleships.services.GameDataServices
import com.example.battleships.services.Mode
import com.example.battleships.utils.hypermedia.SirenAction
import com.example.battleships.utils.hypermedia.SirenLink
import kotlinx.coroutines.delay
import com.example.battleships.game.domain.board.Coordinate
import pt.isel.daw.dawbattleshipgame.domain.game.*
import pt.isel.daw.dawbattleshipgame.domain.player.Player
import pt.isel.daw.dawbattleshipgame.domain.ship.Orientation
import pt.isel.daw.dawbattleshipgame.domain.ship.ShipType

const val GAME_ID = 333
const val PLAYER1_ID = 1
const val PLAYER2_ID = 2

class FakeGameDataServices : GameDataServices {
    private val configuration = Configuration(
        boardSize = 10,
        fleet = mapOf(
            Pair(ShipType.CARRIER, 5),
            Pair(ShipType.BATTLESHIP, 4),
            Pair(ShipType.CRUISER, 3),
            Pair(ShipType.SUBMARINE, 3),
            Pair(ShipType.DESTROYER, 2)
        ),
        shots = 10,
        roundTimeout = 10
    )

    var game: Game? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun createGame(
        token: String,
        mode: Mode,
        CreateGameAction: SirenAction?,
        configuration: Configuration?
    ): Either<ApiException, Boolean> {
        val newGame = Game.newGame(
            GAME_ID,
            PLAYER1_ID,
            PLAYER2_ID,
            configuration ?: Configuration.DEFAULT
        )
        game = setEnemyShipLayout(newGame)
        return Either.Right(true)
    }

    private fun setEnemyShipLayout(game: Game): Game {
        var newGame = game
        val ships = game.configuration.fleet.entries
        ships.forEachIndexed { idx, ship ->
            val shipType = ship.key
            val shipOrientation = Orientation.HORIZONTAL
            val shipPosition = Coordinate((idx+1) * 2, 1)
            newGame = newGame.placeShip(shipType, shipPosition, shipOrientation, Player.TWO)
                ?: throw java.lang.IllegalStateException("Ship placement failed")
        }
        return newGame.confirmFleet(Player.TWO) ?: throw java.lang.IllegalStateException("Fleet confirmation failed")
    }

    override suspend fun getCurrentGameId(
        token: String,
        GetCurrentGameIdLink: SirenLink?,
        mode: Mode
    ): Either<ApiException, Int?> {
        return Either.Right(game?.id)
    }

    override suspend fun setFleet(
        token: String,
        ships: List<Triple<ShipType, Coordinate, Orientation>>,
        newSetFleetAction: SirenAction?,
        mode: Mode
    ): Either<ApiException, Boolean> {
        var newGame = game
        ships.forEach { (shipType, coordinate, orientation) ->
            newGame = newGame?.placeShip(shipType, coordinate, orientation, Player.ONE) ?: return Either.Right(false)
        }
        game = newGame?.confirmFleet(Player.ONE) ?: return Either.Right(false)
        return Either.Right(true)
    }

    override suspend fun placeShots(
        token: String,
        shots: ShotsList,
        PlaceShotAction: SirenAction?,
        mode: Mode
    ): Either<ApiException, Boolean> {
        val game = game ?: return Either.Right(false)
        val newGame = game.placeShots(game.player1, shots.shots, Player.ONE) ?: return Either.Right(false)
        this.game = newGame
        if (newGame.state == GameState.FINISHED) return Either.Right(true)
        delay(100)
        shootWithEnemy()
        return Either.Right(true)
    }

    private fun shootWithEnemy() {
        val game = game ?: return
        if (game.state == GameState.FINISHED) return
        // tries to shoot with enemy, on a random coordinate, until it succeeds
        while (true) {
            val randomCoordinate = Coordinate(
                (1..configuration.boardSize).random(),
                (1..configuration.boardSize).random()
            )
            val newGame = game.placeShot(game.player2, randomCoordinate, Player.TWO) ?: continue
            this.game = newGame
            if (newGame.state == GameState.FINISHED) return
            break
        }
    }

    override suspend fun getGame(
        token: String,
        GetGameLink: SirenLink?,
        mode: Mode
    ): Either<ApiException, Pair<Game, Player>?> {
        return Either.Right(game?.let { Pair(it, Player.ONE) })
    }

    override suspend fun checkIfUserIsInQueue(
        token: String,
        UserInQueueLink: SirenLink?,
        mode: Mode
    ): Either<ApiException, Boolean> {
        return Either.Right(false)
    }

    override suspend fun surrender(
        token: String,
        SurrenderAction: SirenAction?,
        mode: Mode
    ): Either<ApiException, Boolean> {
        return Either.Right(false)
    }
}