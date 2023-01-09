package com.example.battleships.game

import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.battleships.game.domain.game.Game
import com.example.battleships.game.domain.game.GameState
import com.example.battleships.game.domain.board.Board
import com.example.battleships.game.domain.board.Coordinate
import pt.isel.daw.dawbattleshipgame.domain.board.Panel
import com.example.battleships.game.domain.game.Configuration
import com.example.battleships.game.domain.game.ShotsList
import com.example.battleships.ui.Button1
import com.example.battleships.ui.Title
import com.example.battleships.ui.TitleSize
import com.example.battleships.ui.theme.SurrenderRed
import com.example.battleships.utils.ErrorAlert
import com.example.battleships.utils.SCREEN_WIDTH
import pt.isel.battleships.R
import pt.isel.daw.dawbattleshipgame.domain.player.Player
import pt.isel.daw.dawbattleshipgame.domain.ship.ShipType

val PLAY_SIDE =
    fun(boardSize: Int) = (SCREEN_WIDTH / ((boardSize + GRID_WIDTH.value) * 2 + boardSize / 1.5)).dp
val DEFAULT_PLAY_SIDE = 30.dp
val GRID_WIDTH = 5.dp

// Test tags for the Rankings screen
const val CarrierShipButtonTestTag = "CarrierShipButton"
const val BattleshipShipButtonTestTag = "BattleshipShipButton"
const val CruiserShipButtonTestTag = "CruiserShipButton"
const val SubmarineShipButtonTestTag = "SubmarineShipButton"
const val DestroyerShipButtonTestTag = "DestroyerShipButton"
const val ConfirmFleetButtonTestTag = "ConfirmFleetButton"
const val SurrenderButtonTestTag = "SurrenderButton"

private const val TAG = "GameViews"


@Composable
fun GameView(
    game: Game,
    player: Player,
    onShipClick: (ShipType) -> Unit,
    onSquarePressed: (Coordinate) -> Boolean,
    onShotsPlaced: (ShotsList) -> Unit,
    onConfirmLayout: () -> Unit,
    onSurrenderRequest: () -> Unit,
) {
    Log.d(TAG, "Composing GameView")
    Column(
        Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val board = getBoard(game, player)
        when {
            game.state === GameState.FLEET_SETUP -> {
                Log.d(TAG, "Composing GameScreen, state: FLEET_SETUP")
                if (!board.isConfirmed())
                    FleetSetup(
                        board,
                        game.configuration,
                        onSquarePressed,
                        onShipClick,
                        onConfirmLayout
                    )
                else WaitingForOpponentToConfirm(board)
            }
            game.state === GameState.BATTLE -> {
                Log.d(TAG, "Composing GameScreen, state: BATTLE")
                val playerIdTurn = game.playerTurn ?: return
                val playerTurn = game.getPlayerFromId(playerIdTurn)
                Battle(
                    player,
                    playerTurn,
                    game.configuration,
                    game.board1,
                    game.board2,
                    onShotsPlaced
                )
            }
            game.state === GameState.FINISHED -> {
                Log.d(TAG, "Composing GameScreen, state: FINISHED")
                val winnerId = game.winner ?: return
                val winner = game.getPlayerFromId(winnerId)
                Winner(winner)
            }
        }
        if (game.state !== GameState.FINISHED) {
            val confirm = remember {
                mutableStateOf(false)
            }
            Button1(
                text = stringResource(id = R.string.game_screen_surrender_button),
                testTag = SurrenderButtonTestTag,
                color = SurrenderRed,
                onClick = {
                    confirm.value = true
                }
            )

            if(confirm.value) {
                ErrorAlert(
                    title = R.string.game_screen_surrender_button,
                    message = R.string.game_screen_surrender_text_confirm,
                    rightButtonText = R.string.yes,
                    onRightButton = { onSurrenderRequest() },
                    leftButtonText = R.string.no,
                    onLeftButton = { confirm.value = false }
                )
            }

        }
    }
}

/**
 * Displays the board.
 */
@Composable
private fun FleetSetup(
    board: Board,
    configuration: Configuration,
    onPanelClick: ((Coordinate) -> Boolean),
    onShipClick: ((ShipType) -> Unit),
    onConfirmLayout: (() -> Unit)
) {
    val clickPanel = remember {
        mutableStateOf(false)
    }
    Title(text = stringResource(id = R.string.game_screen_placing_phase), TitleSize.H4)
    MySpacer()
    BoardView(board, onPanelClick = onPanelClick, panelClicked = clickPanel)
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .offset(y = GRID_WIDTH * 2)
                .padding(15.dp)
        ) {
            ShipsView(configuration, onShipClick, clickPanel)
        }
        Row(
            Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Button1(
                text = "Confirm",
                testTag = ConfirmFleetButtonTestTag,
                onClick = onConfirmLayout
            )
        }
    }
}

@Composable
private fun WaitingForOpponentToConfirm(board: Board) {
    BoardView(board, onPanelClick = null)
    Text(
        text = stringResource(id = R.string.game_screen_waiting_for_opponent_to_confirm_error),
        fontSize = 40.sp
    )
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
    Title(text = stringResource(id = R.string.game_screen_battle_phase), TitleSize.H4)
    MySpacer()

    val shots = remember {
        mutableStateOf(ShotsList(emptyList()))
    }
    val firstCall = remember { // safe val to prevent calling onShot more than once
        mutableStateOf(true)
    }

    fun onCoordinateClick(c: Coordinate): Boolean {
        return if (shots.value.shots.size < configuration.shots.toInt()) {
            shots.value = ShotsList(shots.value.shots.toMutableList().plus(c))
            true
        } else false
    }

    if (player != turn && firstCall.value) {
        shots.value = ShotsList(emptyList())
        onShot(shots.value) // trigger to start fetching game (see onShot function)
        firstCall.value = false
    }

    val displayedBoard = remember { mutableStateOf(player) }
    val clickAction = remember { mutableStateOf<((Coordinate) -> Boolean)?>(::onCoordinateClick) }
    clickAction.value =
        if (turn === player && displayedBoard.value === player.other()) ::onCoordinateClick else null
    Column(
        Modifier.fillMaxWidth()
    ) {
        Text(
            text =
                if (turn == player) stringResource(id = R.string.game_screen_your_turn_msg)
                else stringResource(id = R.string.game_screen_opponents_turn_msg),
            fontSize = 20.sp,
            modifier = Modifier.padding(16.dp),
            color = if (turn == player) Color.Blue else Color.Black
        )
        BoardView(
            if (displayedBoard.value === player) {
                when (player) {
                    Player.ONE -> player1Board
                    Player.TWO -> player2Board
                }
            } else when (player) {
                Player.ONE -> player2Board
                Player.TWO -> player1Board
            },
            viewShips = displayedBoard.value == player,
            clickAction.value
        )
        Row(
            Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {

            OutlinedButton(onClick = {
                displayedBoard.value = displayedBoard.value.other()
                clickAction.value = if (clickAction.value == null) ::onCoordinateClick else null
            }) {
                Text(
                    text = if (displayedBoard.value == player) stringResource(id = R.string.game_screen_opponent_board)
                        else stringResource(id = R.string.game_screen_my_board)
                )
            }

            if (turn == player && displayedBoard.value != player) {
                if((configuration.shots - shots.value.shots.size) == 0L) {
                    OutlinedButton(onClick = { onShot(shots.value).also {
                        firstCall.value = true
                        shots.value = ShotsList(emptyList())
                    } }) {
                        Text(stringResource(id = R.string.game_screen_place_shots))
                    }
                }
                else {
                    OutlinedButton(onClick = { /*NOTHING TO DO*/ }) {
                        Text("${stringResource(id = R.string.game_screen_place_shots)} = ${(configuration.shots - shots.value.shots.size)}")
                    }
                }
            }
        }
    }
}

@Composable
private fun Winner(winner: Player) {
    Title(text = stringResource(id = R.string.game_screen_end_phase), TitleSize.H3)
    Title(text = "${stringResource(id = R.string.game_screen_winner)} $winner", TitleSize.H4)
}

@Composable
private fun BoardView(
    board: Board,
    viewShips: Boolean = true,
    onPanelClick: ((Coordinate) -> Boolean)?,
    panelClicked : MutableState<Boolean>? = null,
) {
    val gameSize = board.dimension
    val playSide = PLAY_SIDE(gameSize)
    val boardSide = playSide * gameSize + GRID_WIDTH * (gameSize - 1)
    Column(
        modifier = Modifier
            .background(MaterialTheme.colors.background)
            .fillMaxWidth()
            .offset(y = (GRID_WIDTH))
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
                SquareView(panel.coordinate, panel, viewShips, playSide) {
                    if (onPanelClick != null) {
                        onPanelClick(panel.coordinate).let {
                            Log.e("Clicked coordinate", panel.coordinate.toString())
                            Log.e("Response" , "Boolean = $it")
                            Log.e("ClickedPanel", panelClicked?.value.toString())
                            if(it && panelClicked != null)
                                panelClicked.value = true
                        }
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
private fun SquareView(
    coordinate: Coordinate,
    panel: Panel,
    viewShips: Boolean,
    playSize: Dp,
    onClick: (() -> Unit)?,
) {
    val color = when (panel.isHit) {
        true -> {
            if (panel.isShip())
                Color.Red
            else Color.Black
        }
        false -> {
            if (panel.isShip() && viewShips)
                Color.Gray
            else Color.Blue
        }
    }
    Box(
        Modifier
            .size(playSize)
            .offset(
                (playSize + GRID_WIDTH) * (coordinate.column - 1),
                (playSize + GRID_WIDTH) * (coordinate.row - 1)
            )
            .background(color)
            .clickable { onClick?.invoke() }
    )
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
internal fun ShipsView(
    configuration: Configuration,
    onShipClick: ((ShipType) -> Unit)?,
    panelClick: MutableState<Boolean>,
) {
    val auxConfig = remember {
        mutableStateOf(configuration.fleet)
    }

    val shipClicked = remember {
        mutableStateOf<ShipType?>(null)
    }

    Row(
        modifier = Modifier
            .horizontalScroll(rememberScrollState())
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        auxConfig.value.forEach { ship ->
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(7.dp)
            ) {

                Text(
                    AnnotatedString(ship.key.name),
                    style = TextStyle(
                        fontSize = 18.sp,
                    )
                )

                ShipComp(configuration, ship.key) {
                    if (onShipClick != null) onShipClick(ship.key).also {
                        shipClicked.value = ship.key
                        panelClick.value = false
                    }
                }

                Log.e("ClickedPanel_fleet", panelClick.value.toString())
                val clicked = shipClicked.value
                if(panelClick.value && clicked != null) {
                    auxConfig.value = auxConfig.value.toMutableMap().minus(clicked)
                    panelClick.value = false
                }
            }
        }
        Spacer(modifier = Modifier.size(30.dp))
    }
}


@Composable
internal fun ShipComp(
    configuration: Configuration, ship: ShipType,
    onClick: () -> Unit
) {
    Row(Modifier.testTag(ship.toTestTag())) {
        Spacer(Modifier.size(GRID_WIDTH))
        println(configuration)
        val shipLength = configuration.getShipLength(ship)
        require(shipLength != null)

        val m = Modifier
            .size(DEFAULT_PLAY_SIDE)
            .background(Color.Gray)

        repeat(shipLength) {
            Box(m.clickable { onClick() })
            Spacer(Modifier.size(GRID_WIDTH))
        }
    }
}

private fun getBoard(game: Game, player: Player): Board {
    return when (player) {
        Player.ONE -> game.board1
        Player.TWO -> game.board2
    }
}

@Composable
private fun MySpacer() {
    Spacer(modifier = Modifier.height(10.dp))
}

@Preview
@Composable
fun WinnerScreenPreview() {
    Winner(winner = Player.ONE)
}