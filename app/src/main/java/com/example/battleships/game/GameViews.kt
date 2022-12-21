package com.example.battleships.game

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.battleships.game.domain.game.Game
import com.example.battleships.game.domain.game.GameState
import com.example.battleships.game.domain.board.Board
import com.example.battleships.game.domain.board.Coordinate
import pt.isel.daw.dawbattleshipgame.domain.board.Panel
import com.example.battleships.game.domain.game.Configuration
import com.example.battleships.game.domain.game.ShotsList
import pt.isel.daw.dawbattleshipgame.domain.player.Player
import pt.isel.daw.dawbattleshipgame.domain.ship.ShipType

val PLAY_SIDE = 30.dp
val GRID_WIDTH = 5.dp

// Test tags for the Rankings screen
const val CarrierShipButtonTestTag = "CarrierShipButton"
const val BattleshipShipButtonTestTag = "BattleshipShipButton"
const val CruiserShipButtonTestTag = "CruiserShipButton"
const val SubmarineShipButtonTestTag = "SubmarineShipButton"
const val DestroyerShipButtonTestTag = "DestroyerShipButton"
const val ConfirmFleetButtonTestTag = "ConfirmFleetButton"


@Composable
fun GameView(
    game: Game,
    player: Player,
    onShipClick: (ShipType) -> Unit,
    onSquarePressed: (Coordinate) -> Unit,
    onShotsPlaced: (ShotsList) -> Unit,
    onConfirmLayout: () -> Unit
) {
    Column(Modifier.fillMaxWidth()) {
        val board = getBoard(game, player)
        when {
            game.state === GameState.FLEET_SETUP -> {
                if (!board.isConfirmed())
                    PreparationPhase(
                        board,
                        game.configuration,
                        onSquarePressed,
                        onShipClick,
                        onConfirmLayout
                    )
                else WaitingForOpponentToConfirm(board)
            }
            game.state === GameState.BATTLE -> {
                val playerIdTurn = game.playerTurn ?: return
                val playerTurn = game.getPlayerFromId(playerIdTurn)
                Battle(player, playerTurn, game.configuration, game.board1, game.board2, onShotsPlaced)
            }
            game.state === GameState.FINISHED -> {
                val winnerId = game.winner ?: return
                val winner = game.getPlayerFromId(winnerId)
                End(winner)
            }
        }
    }
}

/**
 * Displays the board.
 */
@Composable
private fun PreparationPhase(
    board: Board,
    configuration: Configuration,
    onPanelClick: ((Coordinate) -> Unit),
    onShipClick: ((ShipType) -> Unit),
    onConfirmLayout: (() -> Unit)
) {
    BoardView(board, onPanelClick)
    Column (
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(Modifier
            .fillMaxWidth()
        ) {
            ShipOptionView(configuration, onShipClick)
        }

        Button(modifier = Modifier.padding(top = 16.dp),
                onClick = onConfirmLayout) {
                Text("Confirm", fontSize = 30.sp)
        }
    }
}

@Composable
private fun WaitingForOpponentToConfirm(board: Board) {
    BoardView(board, null)
    Text("Waiting for opponent to confirm", fontSize = 40.sp)
}

@Composable
private fun Battle(
    player: Player,
    turn: Player,
    configuration: Configuration,
    player1Board: Board,
    player2Board: Board,
    onShot: (ShotsList) -> Unit,
) {
    val shots = remember {
        mutableStateOf(ShotsList(emptyList()))
    }
    fun onCoordinateClick(c : Coordinate) {
        if(shots.value.shots.size >= configuration.shots.toInt()){
            shots.value = ShotsList(shots.value.shots.toMutableList().plus(c))
        }
    }

    val displayedBoard = remember { mutableStateOf(player.other())}
    val clickAction = remember { mutableStateOf<((Coordinate) -> Unit)?>(::onCoordinateClick) }
    clickAction.value = if (turn === player && displayedBoard.value === player.other()) ::onCoordinateClick else null
    Column(
        Modifier
            .fillMaxWidth()
            .fillMaxSize()
    ) {
        Text(
            text = "Turn: ${turn.name}",
            fontSize = 40.sp,
            modifier = Modifier.padding(16.dp)
        )
        Text(
            text = if (displayedBoard.value === player) "Your Board" else "Opponent Board",
            fontSize = 40.sp,
            modifier = Modifier.padding(16.dp)
        )
        BoardView(
            if (displayedBoard.value === Player.ONE) player1Board else
            player2Board //set default board
        ){
            clickAction.value
        }

        OutlinedButton(onClick = { onShot(shots.value)}) {
            Text("Place Shots")
        }

        TextButton(onClick = {
            displayedBoard.value = displayedBoard.value.other()
            clickAction.value = if (clickAction.value == null) ::onCoordinateClick else null
        }) {
            Text("Switch board")
        }
    }
}

@Composable
private fun End(winner: Player) {
    val fontSize = 40.sp
    val modifier = Modifier.padding(16.dp)
    Text("Game over", modifier = modifier, fontSize = fontSize)
    Text("Winner: $winner", modifier = modifier, fontSize = fontSize)
}

@Composable
private fun BoardView(board: Board, onPanelClick: ((Coordinate) -> Unit)?) {
    val gameSize = board.dimension
    val boardSide = PLAY_SIDE * gameSize + GRID_WIDTH * (gameSize - 1)
    Column(
        modifier = Modifier
            .background(MaterialTheme.colors.background)
            .fillMaxWidth()
            .height(boardSide),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .background(MaterialTheme.colors.background)
                .size(boardSide),
        ) {
            board.board.forEach { panel ->
                ShipOptionView(panel.coordinate, panel) {
                    if (onPanelClick != null) {
                        onPanelClick(panel.coordinate)
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
private fun ShipOptionView(coordinate: Coordinate, panel: Panel, onClick: (() -> Unit)?) {
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

/**
 * Displays a single panel
 */
@Composable
private fun ShotOptionView(coordinate: Coordinate, panel: Panel, onClick: (() -> Unit)?) {
    val color = if (panel.shipType != null) Color.Gray else Color.Blue
    val m = Modifier
        .size(PLAY_SIDE)
        .offset(
            (PLAY_SIDE + GRID_WIDTH) * (coordinate.column - 1),
            (PLAY_SIDE + GRID_WIDTH) * (coordinate.row - 1)
        )
        .background(color)
    Box(m.clickable {
        if (onClick != null) {
            onClick()
        }
    })
    if (panel.isHit) {
        Box(
            modifier = m
                .background(Color.Red)
                .padding(5.dp)
        )
    }
}


@Composable
internal fun ShipOptionView(configuration: Configuration, ship: ShipType, onClick: () -> Unit) {
    Row(Modifier.testTag(ship.toTestTag())) {
        Spacer(Modifier.size(GRID_WIDTH))
        val shipLength = configuration.getShipLength(ship)
        require(shipLength != null)
        repeat(shipLength) {
            val m = Modifier
                .size(PLAY_SIDE)
                .background(Color.Gray)
            Box(m.clickable { onClick() })
            Spacer(Modifier.size(GRID_WIDTH))
        }
    }
}

private fun ShipType.toTestTag() = when (this) {
    ShipType.BATTLESHIP -> BattleshipShipButtonTestTag
    ShipType.CARRIER -> CarrierShipButtonTestTag
    ShipType.DESTROYER -> DestroyerShipButtonTestTag
    ShipType.SUBMARINE -> SubmarineShipButtonTestTag
    ShipType.CRUISER -> CruiserShipButtonTestTag
}

/**
 * Draws all ships in the option menu.
 */
@Composable
internal fun ShipOptionView(configuration: Configuration, onShipClick: ((ShipType) -> Unit)?) {
    Row(
        modifier = Modifier
            .horizontalScroll(rememberScrollState())
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
    ){
        configuration.fleet.forEach { ship ->
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    AnnotatedString(ship.key.name),
                    style = TextStyle(
                        fontSize = 18.sp,
                    )
                )
                Spacer(modifier = Modifier.size(13.dp))
                ShipOptionView(configuration, ship.key) { if (onShipClick != null) onShipClick(ship.key) }
            }
            Spacer(modifier = Modifier.size(30.dp))
        }
    }
}

private fun getBoard(game: Game, player: Player): Board {
    return when (player) {
        Player.ONE -> game.board1
        Player.TWO -> game.board2
    }
}