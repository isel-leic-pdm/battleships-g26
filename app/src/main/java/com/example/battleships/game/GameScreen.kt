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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.battleships.game.domain.game.*
import com.example.battleships.game.domain.ship.getOrientation
import com.example.battleships.ui.theme.BattleshipsTheme
import pt.isel.battleships.R
import com.example.battleships.game.domain.board.Coordinate
import com.example.battleships.info.InfoScreen
import com.example.battleships.ui.*
import com.example.battleships.utils.*
import pt.isel.daw.dawbattleshipgame.domain.player.Player
import pt.isel.daw.dawbattleshipgame.domain.ship.Orientation
import pt.isel.daw.dawbattleshipgame.domain.ship.ShipType

const val QuickGame = "QuickGame"
const val NewGame = "NewGame"
const val RestoreGame = "RestoreGame"

internal open class Selection
internal class ShipOption(val shipType: ShipType) : Selection()
internal class Square(val coordinate: Coordinate) : Selection()

private const val TAG = "GameScreen"

@Composable
internal fun GameScreen(
    activity: GameActivity,
    onBackRequest: () -> Unit,
) {
    Log.d(TAG, "Composing GameScreen")
    val mContext = LocalContext.current
    BattleshipsTheme {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .testTag("GameScreen"),
            backgroundColor = MaterialTheme.colors.background,
            topBar = {
                TopBar(
                    title = stringResource(id = R.string.game_screen_top_app_bar_title),
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
                        rightButtonText = R.string.error_exit_button_text,
                        onRightButton = { onBackRequest() }
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
    val context = LocalContext.current

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
        onShotsPlaced = {
            activity.vm.placeShots(it, ApiErrorHandler(context))
        },
        onConfirmLayout = {
            game.getBoard(player).getShips().map { ship ->
                Triple(
                    ship.type,
                    ship.coordinates.minByOrNull {
                        (it.row * game.configuration.boardSize) + it.column
                    }!!, // this will choose the first/lower coordinate
                    ship.getOrientation()
                )
            }.let { ships ->
                activity.vm.setFleet(ships, ApiErrorHandler(context))
            }
        },
    )
}

@Composable
private fun InitScreen(activity: GameActivity) {
    val config = remember { mutableStateOf(false) }
    val boardSize = remember { mutableStateOf(10F) }
    val context = LocalContext.current
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

    Title(text = stringResource(id = R.string.game_screen_options_title), TitleSize.H4)

    Button1(text = stringResource(id = R.string.game_screen_option_1), testTag = QuickGame) {
        activity.vm.startGame(errorHandler = ApiErrorHandler(context))
    }
    Button1(text = stringResource(id = R.string.game_screen_option_2), testTag = NewGame) {
        config.value = true
    }
    Button1(text = stringResource(id = R.string.game_screen_option_3), testTag = RestoreGame) {
        activity.vm.restoreGame()
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
                                title = stringResource(id = R.string.game_screen_config_option_1),
                                v = boardSize,
                                range = 8F..13F
                            )
                            CustomSlider(
                                title = stringResource(id = R.string.game_screen_config_option_2),
                                v = shots, range = 1F..5F
                            )
                            CustomSlider(
                                title = stringResource(id = R.string.game_screen_config_option_3),
                                v = roundTimeout,
                                range = 10F..240F
                            )
                            OutlinedButton(border = BorderStroke(0.dp, Color.Unspecified),
                                onClick = { chooseFleet.value = true }) {
                                Text(stringResource(id = R.string.game_screen_set_fleet_option))
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
                                    onClick = {
                                        activity.vm.startGame(
                                                Configuration(
                                                    boardSize.value.toInt(),
                                                    fleet = fleet.value.filter {
                                                        it.value.second
                                                    }.entries.associate {
                                                        it.key to it.value.first
                                                    },
                                                    shots = shots.value.toInt()
                                                        .toLong(), //to int to cut all the floating point values
                                                    roundTimeout = roundTimeout.value.toInt()
                                                        .toLong()
                                                ).also { Log.e("Config", it.toString()) },
                                        ApiErrorHandler(context))
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
            Row(modifier = Modifier.size(GRID_WIDTH * 2)) {}
            Text(
                AnnotatedString(ship.key.name),
                style = TextStyle(
                    fontSize = 18.sp,
                )
            )
            Spacer(modifier = Modifier.size(13.dp))

            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {

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
                            .size(DEFAULT_PLAY_SIDE)
                            .background(shipColor)
                    )
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
                        .size(DEFAULT_PLAY_SIDE)
                        .clickable {
                            val fleetV = fleet.value.getValue(ship.key)
                            auxMap[ship.key] = Pair(fleetV.first, !fleetV.second)
                            fleet.value = auxMap
                        }
                )
            }
            Spacer(modifier = Modifier.size(DEFAULT_PLAY_SIDE))
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
            .size(DEFAULT_PLAY_SIDE)
            .clip(RoundedCornerShape(3.dp))
            .border(BorderStroke(1.dp, Color.Gray))
            .clickable { func() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            title, style = TextStyle(
                color = Color.Black,
                fontSize = 23.sp
            )
        )
    }

}

@Composable
private fun CreatingGame() {
    Text(stringResource(id = R.string.game_screen_creating_game))
}

@Composable
private fun Matchmaking() {
    Text(stringResource(id = R.string.game_screen_matchmaking))
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
            activity.vm.setGame(newGame, player)
            // updates game locally
            return null
        }
    }
    if (selected is Square) {
        return if (coordinate == selected.coordinate) {
            val newGame = curGame.rotateShip(coordinate, player) ?: return null // validates
            activity.vm.setGame(newGame, player)
            // updates game locally
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