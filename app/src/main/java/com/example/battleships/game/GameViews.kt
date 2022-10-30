package com.example.battleships.game

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.battleships.game.domain.board.Board
import com.example.battleships.game.domain.board.Coordinate
import com.example.battleships.game.domain.board.Panel
import com.example.battleships.game.domain.ship.ShipType
import com.example.battleships.game.domain.state.*
import com.example.battleships.game.domain.state.single.PlayerPreparationPhase
import com.example.battleships.game.domain.state.single.Single

val PLAY_SIDE = 30.dp
val GRID_WIDTH = 5.dp


@Composable
fun GameView(
    userId: Int,
    game: Game?,
    onShipClick: (ShipType) -> Unit,
    onShipPlaced: (Coordinate) -> Unit,
    onShotPlaced: (Coordinate) -> Unit,
    onConfirmLayout: () -> Unit
) {
    game?.let { game ->
        Column(Modifier.fillMaxWidth()) {
            when (game) {
                is SinglePhase -> {
                    if (game.player1Game is PlayerPreparationPhase)
                        PreparationPhase(
                            game.player1Game,
                            game.configuration,
                            onShipPlaced,
                            onShipClick,
                            onConfirmLayout
                        )
                    else
                        PreparationPhase(game.player1Game, game.configuration, null, null, null)
                }
                is BattlePhase -> Battle(userId, game, onShotPlaced)
                is EndPhase -> End(userId, game)
            }
        }
    }
}

/**
 * Displays the board.
 */
@Composable
private fun PreparationPhase(
    game: Single,
    configuration: Configuration,
    onPanelClick: ((Coordinate) -> Unit)?,
    onShipClick: ((ShipType) -> Unit)?,
    onConfirmLayout: (() -> Unit)?
) {
    val isClickable = onConfirmLayout != null
    BoardView(game.board, if (isClickable) onPanelClick else null)
    Row {
        Box(modifier = Modifier.weight(1f)) {
            ShipOptionView(configuration, if (isClickable) onShipClick else null)
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

enum class Player { PLAYER1, PLAYER2 }

@Composable
private fun Battle(
    userId: Int,
    game: BattlePhase,
    onShot: (Coordinate) -> Unit
) {
    val boardToDisplay = remember { mutableStateOf(
        if (game.player1 == userId) Player.PLAYER2 else Player.PLAYER1
    )}
    val clickAction = remember { mutableStateOf<((Coordinate) -> Unit)?>(onShot) }

    Column(
        Modifier.verticalScroll(rememberScrollState())
    ) {
        val isMyBoardDisplayed = clickAction.value != null
        Text(
            text = if (isMyBoardDisplayed) "Place Shot" else "Your Board",
            fontSize = 40.sp,
            modifier = Modifier.padding(16.dp)
        )
        BoardView(
            if (boardToDisplay.value === Player.PLAYER1) game.player1Board else game.player2Board,
            clickAction.value
        )
        TextButton(onClick = {
            boardToDisplay.value = if (boardToDisplay.value === Player.PLAYER1) Player.PLAYER2 else Player.PLAYER1
            clickAction.value = if (clickAction.value == null) onShot else null
        }) {
            Text("Switch board")
        }
    }
}

@Composable
private fun End(userId: Int, game: EndPhase) {
    Text("Game over")
    Text(text = "Winner: ${game.winner}")
}

@Composable
private fun BoardView(board: Board, onPanelClick: ((Coordinate) -> Unit)?) {
    val gameSize = board.dimension
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
            board.board.forEach { panel ->
                PlayView(panel.coordinate, panel) {
                    onPanelClick?.invoke(panel.coordinate)
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
    val color = if (panel.shipType != null) Color.Gray else Color.Blue
    val m = Modifier
        .size(PLAY_SIDE)
        .offset(
            (PLAY_SIDE + GRID_WIDTH) * (coordinate.column - 1),
            (PLAY_SIDE + GRID_WIDTH) * (coordinate.row - 1)
        )
        .background(color)
    Box(m.clickable { onClick?.invoke() })
    if (panel.isHit) {
        Box(
            modifier = m
                .background(Color.Red)
                .padding(5.dp)
        )
    }
}

@Composable
private fun PlayView(configuration: Configuration, ship: ShipType, onClick: () -> Unit) {
    Row {
        Spacer(Modifier.size(GRID_WIDTH))
        repeat(configuration.getShipLength(ship)) {
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
private fun ShipOptionView(configuration: Configuration, onShipClick: ((ShipType) -> Unit)?) {
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
            PlayView(configuration, ship) { if (onShipClick != null) onShipClick(ship) }
        }
    }
}