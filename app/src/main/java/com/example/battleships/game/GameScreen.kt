package com.example.battleships.game

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.battleships.game.domain.game.*
import com.example.battleships.ui.TopBar
import com.example.battleships.ui.theme.BattleshipsTheme
import pt.isel.daw.dawbattleshipgame.domain.board.Coordinate
import com.example.battleships.game.domain.ship.getOrientation
import pt.isel.daw.dawbattleshipgame.domain.player.Player
import pt.isel.daw.dawbattleshipgame.domain.ship.Orientation
import pt.isel.daw.dawbattleshipgame.domain.ship.ShipType

internal open class Selection
internal class ShipOption(val shipType: ShipType) : Selection()
internal class Square(val coordinate: Coordinate) : Selection()

@Composable
internal fun GameScreen(
    activity: GameActivity
) {
    BattleshipsTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            backgroundColor = MaterialTheme.colors.background,
            topBar = { TopBar() }
        ) { padding ->
            Column(
                verticalArrangement = Arrangement.SpaceAround,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
            ) {
                var selected: Selection? = null
                val curGame = activity.vm.game.value
                val player = activity.vm.player

                if (curGame != null && player != null) {
                    GameView(
                        game = curGame,
                        player = player,
                        onShipClick = {
                            selected = ShipOption(it)
                        },
                        onSquarePressed = {
                            selected = onSquarePressed(selected, curGame, activity, it) ?: return@GameView
                        },
                        onShotPlaced = {
                            val game = activity.vm.game.value ?: return@GameView
                            val playerId = if (player == Player.ONE) game.player1 else game.player2
                            game.placeShot(playerId, it, player) ?: return@GameView // TODO -> shouldn't require playerId
                            activity.vm.placeShot(it)
                        },
                        onConfirmLayout = {
                            val game = activity.vm.game.value ?: return@GameView
                            try { // TODO: change confirmFleet to return Game?, if invalid
                                game.confirmFleet(player) // checks if its possible to confirm the current fleet state
                            } catch (e: Exception) {
                                Log.i("GameScreen", "Invalid fleet layout. Could not confirm.")
                                return@GameView
                            }
                            game.getBoard(player).getShips().map { ship ->
                                Triple(
                                    ship.type,
                                    ship.coordinates.sortedBy { (it.row * game.configuration.boardSize) + it.column }.first(), // this will choose the first/lower coordinate
                                    ship.getOrientation()
                                )
                            }.let { ships ->
                                activity.vm.setFleet(ships)
                            }
                        }
                    )
                }
            }
        }
    }
}

/**
 * Handles the view controller when the user is presses a square.
 * If a ship is selected, it will place the ship on the board.
 * If the same ship is selected, it will rotate the ship.
 * If a ship is selected and another panel is selected, it will move the ship to the new panel.
 * Note: before making any changes, it will check if the move is valid.
 * @return the new resulted selection
 */
private fun onSquarePressed(
    selected: Selection?,
    curGame: Game,
    activity: GameActivity,
    coordinate: Coordinate
): Selection? {
    if (selected == null /*&& curGame.isShip(coordinate)*/) {
        return Square(coordinate)
    } else {
        if (selected is ShipOption) {
            val newGame = curGame.placeShip(selected.shipType, coordinate, Orientation.HORIZONTAL) ?: return null // validates
            activity.vm.setGame(newGame) // updates game locally
            return null
        }
        if (selected is Square) {
            return if (coordinate == selected.coordinate) {
                val newGame = curGame.rotateShip(coordinate) ?: return null // validates
                activity.vm.setGame(newGame) // updates game locally
                null
            } else {
                val newGame = curGame.moveShip(selected.coordinate, coordinate) ?: return null // validates
                activity.vm.setGame(newGame) // updates game locally
                null
            }
        }
        return null
    }
}