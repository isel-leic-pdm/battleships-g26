package com.example.battleships.game

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.fleetbattletemp.game.domain.board.Coordinate
import com.example.fleetbattletemp.game.domain.board.Panel
import com.example.fleetbattletemp.game.domain.board.ShipPanel
import com.example.fleetbattletemp.game.domain.game.Game
import com.example.fleetbattletemp.game.domain.game.State
import com.example.fleetbattletemp.game.domain.ship.ShipType

val PLAY_SIDE = 30.dp
val GRID_WIDTH = 5.dp


@Composable
fun GameView(
    game: Game?,
    onShipClick: (ShipType) -> Unit,
    onShipPlaced: (Coordinate) -> Unit,
    onShotPlaced: (Coordinate) -> Unit,
    onConfirmLayout: () -> Unit
) {
    Column(Modifier.fillMaxWidth()) {
        val gameState = game?.state
        when {
            gameState === State.WARMUP -> Warmup(game, onShipPlaced, onShipClick, onConfirmLayout)
            gameState === State.WAITING -> Warmup(game, null, null, null)
            gameState === State.BATTLE -> Battle(game, onShotPlaced)
            else -> Text("Game is null")
        }
    }
}

/**
 * Displays the board.
 */
@Composable
private fun Warmup(
    game: Game,
    onPanelClick: ((Coordinate) -> Unit)?,
    onShipClick: ((ShipType) -> Unit)?,
    onConfirmLayout: (() -> Unit)?
) {
    val isClickable = onConfirmLayout != null
    BoardView(game, if (isClickable) onPanelClick else null)
    Row {
        Box(modifier = Modifier.weight(1f)) {
            ShipOptionView(game, if (isClickable) onShipClick else null)
        }
        Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.TopCenter) {
            if (isClickable) {
                TextButton(
                    modifier = Modifier.padding(top = 16.dp),
                    onClick = onConfirmLayout!!
                ) {
                    Text("Confirm", fontSize = 40.sp)
                }
            }
        }
    }
}

@Composable
private fun Battle(game: Game, onShot: (Coordinate) -> Unit) {
    BoardView(game, onShot)
}

@Composable
private fun BoardView(game: Game, onPanelClick: ((Coordinate) -> Unit)?) {
    game.let { _game ->
        val gameSize = _game.configuration.boardSize
        val boardSide = PLAY_SIDE * gameSize + GRID_WIDTH * (gameSize - 1)
        Column(
            modifier = Modifier
                .background(Color.Black)
                .fillMaxWidth()
                .height(boardSide),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .background(Color.Black)
                    .size(boardSide),
            ) {
                _game.boardCoordinates.forEach { pos ->
                    PlayView(pos, _game[pos]) {
                        onPanelClick?.invoke(pos)
                    }
                }
            }
        }
    }
}

/**
 * Displays a single panel
 */
@Composable
private fun PlayView(coordinate: Coordinate, panel: Panel, onClick: (() -> Unit)?) {
    val color = if (panel is ShipPanel) Color.Gray else Color.Blue
    val m = Modifier
        .size(PLAY_SIDE)
        .offset(
            (PLAY_SIDE + GRID_WIDTH) * (coordinate.column - 1),
            (PLAY_SIDE + GRID_WIDTH) * (coordinate.row - 1)
        )
        .background(color)
    Box(m.clickable { onClick?.invoke() })
}

@Composable
private fun PlayView(game: Game, ship: ShipType, onClick: () -> Unit) {
    Row {
        Spacer(Modifier.size(GRID_WIDTH))
        repeat(game.configuration.getShipLength(ship)) {
            val m = Modifier
                .size(PLAY_SIDE)
                .background(Color.Gray)
            Box(m.clickable { onClick() })
            Spacer(Modifier.size(GRID_WIDTH))
        }
    }
}

/**
 * Draws all ships in the option menu.
 */
@Composable
private fun ShipOptionView(game: Game, onShipClick: ((ShipType) -> Unit)?) {
    Column(
        modifier = Modifier
            .width(200.dp)
            .background(Color.Black)
            .fillMaxHeight()
    ) {
        ShipType.values().forEach { ship ->
            Text(
                AnnotatedString(ship.name),
                style = TextStyle(
                    fontSize = 26.sp,
                    color = Color.White
                )
            )
            Spacer(modifier = Modifier.size(10.dp))
            PlayView(game, ship) { if (onShipClick != null) onShipClick(ship) }
        }
    }
}