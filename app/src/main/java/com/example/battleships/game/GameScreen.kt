package com.example.battleships.game

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import com.example.battleships.game.domain.game.*
import com.example.battleships.game.domain.ship.getOrientation
import com.example.battleships.ui.NavigationHandlers
import com.example.battleships.ui.TopBar
import com.example.battleships.ui.theme.BattleshipsTheme
import com.example.battleships.utils.ErrorAlert
import com.example.battleships.utils.getWith
import pt.isel.battleships.R
import pt.isel.daw.dawbattleshipgame.domain.board.Coordinate
import pt.isel.daw.dawbattleshipgame.domain.player.Player
import pt.isel.daw.dawbattleshipgame.domain.ship.Orientation
import pt.isel.daw.dawbattleshipgame.domain.ship.ShipType

internal open class Selection
internal class ShipOption(val shipType: ShipType) : Selection()
internal class Square(val coordinate: Coordinate) : Selection()

@Composable
internal fun GameScreen(
    activity: GameActivity,
    onBackRequest: () -> Unit,
) {
    val mContext = LocalContext.current
    BattleshipsTheme {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .testTag("GameScreen"),
            backgroundColor = MaterialTheme.colors.background,
            topBar = { TopBar(
                navigation = NavigationHandlers(
                    onBackRequested = onBackRequest
                )
            ) }
        ) { padding ->
            Column(
                verticalArrangement = Arrangement.SpaceAround,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
            ) {
                when (val game = activity.vm.game.getWith(mContext)) {
                    is GameViewModel.NotCreated -> InitScreen(activity)
                    is GameViewModel.Creating -> CreatingGame()
                    is GameViewModel.Matchmaking -> Matchmaking()
                    is GameViewModel.Started -> PlayScreen(activity, game)
                    else -> ErrorAlert(
                        title = R.string.error_api_title,
                        message = R.string.error_could_not_reach_api,
                        buttonText = R.string.error_retry_button_text,
                        onDismiss = { onBackRequest() }
                    )
                }
            }
        }
    }
}

@Composable
private fun InitScreen(activity: GameActivity) {
    Button(onClick = { /*TODO*/ }) {
        Text("Quick Game")
    }

    Button(onClick = { activity.vm.startGame() }) {
        Text("Start New Game")
    }
    Button(onClick = { activity.vm.restoreGame() }) {
        Text("Restore Previous Game")
    }
}

@Composable
private fun CreatingGame() {
    Text("Creating Game...")
}

@Composable
private fun Matchmaking() {
    Text("Matchmaking...")
}

@Composable
private fun PlayScreen(
    activity: GameActivity,
    gameState: GameViewModel.Started
) {
    var selected: Selection? = null
    val game = gameState.gameResultInternal.game
    val player = gameState.gameResultInternal.player

    GameView(
        game = game,
        player = player,
        onShipClick = {
            selected = ShipOption(it)
        },
        onSquarePressed = {
            selected = onSquarePressed(selected, game, player, activity, it)
                ?: return@GameView
        },
        onShotPlaced = {
            val playerId =
                if (player == Player.ONE) game.player1 else game.player2
            game.placeShot(playerId, it, player)
                ?: return@GameView // TODO -> shouldn't require playerId
            activity.vm.placeShot(it)
        },
        onConfirmLayout = {
            game.confirmFleet(player)
                ?: return@GameView // checks if its possible to confirm the current fleet state
            game.getBoard(player).getShips().map { ship ->
                Triple(
                    ship.type,
                    ship.coordinates.minByOrNull {
                        (it.row * game.configuration.boardSize) + it.column
                    }!!, // this will choose the first/lower coordinate
                    ship.getOrientation()
                )
            }.let { ships ->
                activity.vm.setFleet(ships)
            }
        }
    )
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
    player: Player,
    activity: GameActivity,
    coordinate: Coordinate
): Selection? {
    if (selected == null /*&& curGame.isShip(coordinate)*/) {
        return Square(coordinate)
    } else {
        if (selected is ShipOption) {
            val newGame = curGame.placeShip(selected.shipType, coordinate, Orientation.HORIZONTAL, player) ?: return null // validates
            activity.vm.setGame(newGame, player) // updates game locally
            return null
        }
        if (selected is Square) {
            return if (coordinate == selected.coordinate) {
                val newGame = curGame.rotateShip(coordinate, player) ?: return null // validates
                activity.vm.setGame(newGame, player) // updates game locally
                null
            } else {
                val newGame = curGame.moveShip(selected.coordinate, coordinate, player) ?: return null // validates
                activity.vm.setGame(newGame, player) // updates game locally
                null
            }
        }
        return null
    }
}