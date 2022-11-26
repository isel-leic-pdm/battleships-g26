package com.example.battleships.services.fake

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.battleships.game.domain.game.Game
import com.example.battleships.game.domain.game.confirmFleet
import com.example.battleships.game.domain.game.placeShip
import com.example.battleships.game.domain.game.placeShot
import com.example.battleships.services.GameDataServices
import com.example.battleships.services.Mode
import com.example.battleships.utils.hypermedia.SirenAction
import com.example.battleships.utils.hypermedia.SirenLink
import kotlinx.coroutines.delay
import pt.isel.daw.dawbattleshipgame.domain.board.Coordinate
import pt.isel.daw.dawbattleshipgame.domain.game.*
import pt.isel.daw.dawbattleshipgame.domain.player.Player
import pt.isel.daw.dawbattleshipgame.domain.ship.Orientation
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
            /*
            Pair(ShipType.BATTLESHIP, 4),
            Pair(ShipType.CRUISER, 3),
            Pair(ShipType.SUBMARINE, 3),
            Pair(ShipType.DESTROYER, 2)
             */
        ),
        nShotsPerRound = 10,
        roundTimeout = TIMEOUT
    )

    var game: Game? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun createGame(
        token: String,
        mode: Mode,
        newCreateGameAction: SirenAction?
    ): Boolean {
        val newGame = Game.newGame(
            GAME_ID,
            PLAYER1_ID,
            PLAYER2_ID,
            configuration
        )
        game = setEnemyShipLayout(newGame)
        return true
    }

    private fun setEnemyShipLayout(game: Game): Game {
        var newGame = game
        val ships = game.configuration.fleet
        ships.forEachIndexed { idx, ship ->
            val shipType = ship.first
            val shipOrientation = Orientation.HORIZONTAL
            val shipPosition = Coordinate((idx+1) * 2, 1)
            newGame = newGame.placeShip(shipType, shipPosition, shipOrientation, Player.TWO)
                ?: throw java.lang.IllegalStateException("Ship placement failed")
        }
        return newGame.confirmFleet(Player.TWO)
    }

    override suspend fun getCurrentGameId(
        token: String,
        mode: Mode,
        newGetCurrentGameIdLink: SirenLink?
    ): Int? {
        return game?.id
    }

    override suspend fun setFleet(
        token: String,
        ships: List<Triple<ShipType, Coordinate, Orientation>>,
        newSetFleetAction: SirenAction?,
        mode: Mode
    ): Boolean {
        var newGame = game
        ships.forEach { (shipType, coordinate, orientation) ->
            newGame = newGame?.placeShip(shipType, coordinate, orientation, Player.ONE) ?: return false
        }
        game = newGame?.confirmFleet(Player.ONE)
        return true
    }

    override suspend fun placeShot(
        token: String,
        coordinate: Coordinate,
        newPlaceShotAction: SirenAction?,
        mode: Mode
    ): Boolean {
        val game = game ?: return false
        this.game = game.placeShot(game.player1, coordinate, Player.ONE)
        delay(1500)
        shootWithEnemy()
        return true
    }

    private fun shootWithEnemy() {
        val game = game ?: return
        // tries to shoot with enemy, on a random coordinate, until it succeeds
        while (true) {
            val randomCoordinate = Coordinate(
                (1..configuration.boardSize).random(),
                (1..configuration.boardSize).random()
            )
            val newGame = game.placeShot(game.player2, randomCoordinate, Player.TWO) ?: continue
            this.game = newGame
            break
        }
    }

    override suspend fun getGame(
        token: String,
        newGetGameLink: SirenLink?,
        mode: Mode
    ): Pair<Game, Player>? {
        return game?.let { Pair(it, Player.ONE) }
    }
}