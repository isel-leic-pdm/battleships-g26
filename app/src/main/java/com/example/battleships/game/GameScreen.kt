package com.example.battleships.game

import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.battleships.game.domain.game.*
import com.example.battleships.game.domain.ship.getOrientation
import com.example.battleships.ui.NavigationHandlers
import com.example.battleships.ui.TopBar
import com.example.battleships.ui.theme.BattleshipsTheme
import com.example.battleships.utils.ErrorAlert
import com.example.battleships.utils.SCREEN_HEIGHT
import com.example.battleships.utils.getWith
import pt.isel.battleships.R
import com.example.battleships.game.domain.board.Coordinate
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
            topBar = {
                TopBar(
                    navigation = NavigationHandlers(
                        onBackRequested = onBackRequest
                    )
                )
            }
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
private fun PlayScreen(
    activity: GameActivity,
    gameState: GameViewModel.Started
) {
    var selected: Selection? = null
    val game = gameState.gameResultInternal.game
    val player = gameState.gameResultInternal.player
    val configuration = gameState.gameResultInternal.game.configuration
    val shots = remember {
        mutableStateOf(Shots(emptyList()))
    }
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
            val playerId = if (player == Player.ONE) game.player1 else game.player2
            game.placeShot(playerId, it, player) ?: return@GameView
            shots.value = Shots(shots.value.shots.toMutableList().plus(it))
            if(shots.value.shots.size == configuration.shots.toInt()){
                activity.vm.placeShots(shots.value)
            }
        },
        onConfirmLayout = {
            game.confirmFleet(player) ?: return@GameView
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

@Composable
private fun InitScreen(activity: GameActivity) {
    val config = remember { mutableStateOf(false) }
    val boardSize = remember { mutableStateOf(10F) }
    val fleet = remember {
        mutableStateOf(
            mapOf(
                ShipType.CARRIER to Pair(5, true),
                ShipType.BATTLESHIP to Pair(4, true),
                ShipType.CRUISER to Pair(3, true),
                ShipType.SUBMARINE to Pair(3, true),
                ShipType.DESTROYER to Pair(2, true),
            )
        )
    }
    val shots = remember { mutableStateOf(1F) }
    val roundTimeout = remember { mutableStateOf(10F) }
    val chooseFleet = remember { mutableStateOf(false) }

    Button(onClick = { activity.vm.startGame() }) {
        Text("Quick Game")
    }
    Button(onClick = { config.value = true }) {
        Text("Start New Game")
    }
    Button(onClick = { activity.vm.restoreGame() }) {
        Text("Restore Previous Game")
    }
    if (config.value) {
        AlertDialog(
            onDismissRequest = {
                config.value = false
                chooseFleet.value = false
            },
            buttons = {
                Surface(
                    shape = MaterialTheme.shapes.medium,
                    color = MaterialTheme.colors.surface
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        if (!chooseFleet.value) {
                            CustomSlider(
                                title = "Board Size", v =
                                boardSize, range = 8F..15F
                            )
                            CustomSlider(
                                title = "Shots per round", v =
                                shots, range = 1F..5F
                            )
                            CustomSlider(
                                title = "Round timeout", v =
                                roundTimeout, range = 10F..240F
                            )
                            OutlinedButton(border = BorderStroke(0.dp, Color.Unspecified),
                                onClick = { chooseFleet.value = true }) {
                                Text("Set Fleet")
                            }
                        } else {
                            Column(
                                Modifier
                                    .height((SCREEN_HEIGHT / 6).dp)
                                    .fillMaxWidth()
                                    .verticalScroll(rememberScrollState())
                            ) {
                                ConfigShipView(fleet = fleet)
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                OutlinedButton(border = BorderStroke(0.dp, Color.Unspecified),
                                    onClick = { chooseFleet.value = false }) {
                                    Text("Go back")
                                }
                                OutlinedButton(border = BorderStroke(0.dp, Color.Unspecified),
                                    onClick = { activity.vm.startGame(
                                        Configuration(
                                            boardSize.value.toInt(),
                                            fleet = fleet.value.filter {
                                                it.value.second
                                            }.entries.associate {
                                                it.key to it.value.first
                                            },
                                            shots = shots.value.toInt().toLong(), //to int to cut all the floating point values
                                            roundTimeout = roundTimeout.value.toInt().toLong()
                                        ).also { Log.e("Config", it.toString()) })
                                    }
                                ) {
                                    Text("Continue")
                                }
                            }

                        }
                    }
                }
            },
            title = { Text("Configuration") },
        )
    }
}


@Composable
private fun CustomSlider(
    title: String,
    v: MutableState<Float>,
    range: ClosedFloatingPointRange<Float>,
    steps: Int = 0,
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(20.dp)
    ) {
        Row {
            Text(text = title + " : ${v.value.toInt()}")
        }
        Slider(
            value = v.value, onValueChange = {
                v.value = it
            }, valueRange = range, steps = steps, colors = SliderDefaults.colors(
                thumbColor = MaterialTheme.colors.primary,
                activeTrackColor = MaterialTheme.colors.primary,
            )
        )
    }
}

@Composable
private fun ConfigShipView(fleet: MutableState<Map<ShipType, Pair<Int, Boolean>>>) {
    val auxMap = fleet.value.toMutableMap()
    fleet.value.forEach { ship ->
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start,
        ) {
            Row(modifier = Modifier.size(GRID_WIDTH * 2)){}
            Text(
                AnnotatedString(ship.key.name),
                style = TextStyle(
                    fontSize = 18.sp,
                )
            )
            Spacer(modifier = Modifier.size(13.dp))

            Row (
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
                    ){

                ActionButton("-") {
                    val fleetV = fleet.value.getValue(ship.key)
                    if (fleetV.first - 1 > 0) {
                        auxMap[ship.key] = Pair(fleetV.first - 1, fleetV.second)
                        fleet.value = auxMap
                    }
                }

                Spacer(Modifier.size(GRID_WIDTH))

                val shipLength = ship.value.first
                val shipColor = if (ship.value.second)
                    Color.Gray else Color.Red

                repeat(shipLength) {
                    Box(
                        Modifier
                            .size(PLAY_SIDE)
                            .background(shipColor))
                    Spacer(Modifier.size(GRID_WIDTH))
                }
                Spacer(Modifier.size(GRID_WIDTH))

                ActionButton(title = "+") {
                    val fleetV = fleet.value.getValue(ship.key)
                    if (fleetV.first + 1 <= 5) {
                        auxMap[ship.key] = Pair(fleetV.first + 1, fleetV.second)
                        fleet.value = auxMap
                    }
                }
                Spacer(Modifier.size(GRID_WIDTH))

                Icon(imageVector = Icons.Default.Clear,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier
                        .size(PLAY_SIDE)
                        .clickable {
                            val fleetV = fleet.value.getValue(ship.key)
                            auxMap[ship.key] = Pair(fleetV.first, !fleetV.second)
                            fleet.value = auxMap
                        }
                )
            }
            Spacer(modifier = Modifier.size(30.dp))
        }
    }
}


@Composable
fun ActionButton(
    title: String,
    func: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(PLAY_SIDE)
            .clip(RoundedCornerShape(3.dp))
            .border(BorderStroke(1.dp, Color.Gray))
            .clickable { func() },
        contentAlignment = Alignment.Center
    ){
        Text(title, style = TextStyle(
            color = Color.Black,
            fontSize = 23.sp
        ))
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
    coordinate: Coordinate,
): Selection? {
    if (selected == null /*&& curGame.isShip(coordinate)*/) {
        return Square(coordinate)
    } else {
        if (selected is ShipOption) {
            val newGame =
                curGame.placeShip(selected.shipType, coordinate, Orientation.HORIZONTAL, player)
                    ?: return null // validates
            activity.vm.setGame(newGame, player) // updates game locally
            return null
        }
        if (selected is Square) {
            return if (coordinate == selected.coordinate) {
                val newGame = curGame.rotateShip(coordinate, player) ?: return null // validates
                activity.vm.setGame(newGame, player) // updates game locally
                null
            } else {
                val newGame = curGame.moveShip(selected.coordinate, coordinate, player)
                    ?: return null // validates
                activity.vm.setGame(newGame, player) // updates game locally
                null
            }
        }
        return null
    }
}