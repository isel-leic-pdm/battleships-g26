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
import com.example.battleships.game.domain.ship.ShipType
import com.example.battleships.game.domain.state.Game
import com.example.battleships.game.domain.state.moveShip
import com.example.battleships.game.domain.state.onSquarePressed
import com.example.battleships.game.domain.state.rotateShip
import com.example.battleships.ui.TopBar
import com.example.battleships.ui.theme.BattleshipsTheme
import pt.isel.daw.dawbattleshipgame.domain.board.Coordinate
import pt.isel.daw.dawbattleshipgame.domain.ship.Orientation

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
                val userId = activity.vm.userId.value
                val player = activity.vm.player.value

                if (curGame != null && userId != null && player != null) {
                    GameView(
                        game = curGame,
                        player = player,
                        onShipClick = {
                            selected = ShipOption(it)
                        },
                        onSquarePressed = { selected = onSquarePressed(selected, curGame, activity, it) },
                        onShotPlaced = { activity.vm.placeShot(it) },
                        onConfirmLayout = { activity.vm.confirmFleet() }
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
            curGame.onSquarePressed(selected.shipType, coordinate, Orientation.HORIZONTAL) ?: return null // validates
            activity.vm.placeShip(
                selected.shipType,
                coordinate,
                Orientation.HORIZONTAL
            )
            return null
        }
        if (selected is Square) {
            return if (coordinate == selected.coordinate) {
                curGame.rotateShip(coordinate) ?: return null // validates
                activity.vm.rotateShip(coordinate)
                null
            } else {
                curGame.moveShip(selected.coordinate, coordinate) ?: return null // validates
                activity.vm.moveShip(selected.coordinate, coordinate)
                null
            }
        }
        return null
    }
}