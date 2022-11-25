package com.example.battleships.game

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.example.battleships.ui.TopBar
import com.example.battleships.ui.theme.BattleshipsTheme
import pt.isel.daw.dawbattleshipgame.domain.board.Coordinate
import com.example.battleships.game.domain.game.Game
import pt.isel.daw.dawbattleshipgame.domain.game.moveShip
import pt.isel.daw.dawbattleshipgame.domain.game.placeShip
import pt.isel.daw.dawbattleshipgame.domain.game.rotateShip
import pt.isel.daw.dawbattleshipgame.domain.ship.Orientation
import pt.isel.daw.dawbattleshipgame.domain.ship.ShipType

internal open class Selection
internal class ShipOption(val shipType: ShipType) : Selection()
internal class Square(val coordinate: Coordinate) : Selection()
internal class PostedShips(val ships: List<Triple<ShipType, Coordinate, Orientation>>) : Selection()


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
                var postedShips = PostedShips(emptyList())
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
                            val res = onSquarePressed(selected, postedShips, curGame, activity, it) ?: return@GameView
                            selected = res.first
                            postedShips = res.second
                        },
                        onShotPlaced = { activity.vm.placeShot(it) },
                        onConfirmLayout = { activity.vm.setFleet(postedShips.ships) }
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
    postedShips: PostedShips,
    curGame: Game,
    activity: GameActivity,
    coordinate: Coordinate
): Pair<Selection?, PostedShips>? {
    if (selected == null /*&& curGame.isShip(coordinate)*/) {
        return Square(coordinate) to postedShips
    } else {
        if (selected is ShipOption) {
            val newGame = curGame.placeShip(selected.shipType, coordinate, Orientation.HORIZONTAL) ?: return null // validates
            activity.vm.setGame(newGame) // updates game locally
            return null to PostedShips(postedShips.ships + Triple(selected.shipType, coordinate, Orientation.HORIZONTAL))
        }
        if (selected is Square) {
            return if (coordinate == selected.coordinate) {
                val newGame = curGame.rotateShip(coordinate) ?: return null // validates
                activity.vm.setGame(newGame) // updates game locally
                // TODO: update postedShips
                null
            } else {
                val newGame = curGame.moveShip(selected.coordinate, coordinate) ?: return null // validates
                activity.vm.setGame(newGame) // updates game locally
                // TODO: update postedShips
                null
            }
        }
        return null
    }
}