package com.example.battleships.game

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.battleships.game.domain.state.BattlePhase
import com.example.battleships.game.domain.state.Game
import com.example.battleships.game.domain.state.GameState
import com.example.battleships.game.domain.state.SinglePhase
import com.example.battleships.game.domain.state.single.PlayerPreparationPhase
import com.example.battleships.game.services.BattleshipsService
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pt.isel.daw.dawbattleshipgame.domain.board.Coordinate
import pt.isel.daw.dawbattleshipgame.domain.ship.Orientation
import pt.isel.daw.dawbattleshipgame.domain.ship.ShipType
import pt.isel.daw.dawbattleshipgame.domain.state.placeShip

/**
 * The ViewModel for BattleshipActivity.
 * This class holds the state of the game and provides the logic to interact with it.
 * Any actions that change the state of the game, will, first, be tested offline and, if valid, will
 * be sent to the server to be persisted. The server will then, test the action again and, if valid,
 * will return the new state of the game. This new state will be used to update the local state.
 */
class GameViewModel(
    private val gameService: BattleshipsService,
    private val token: String
) : ViewModel() {
    private val _userId: MutableState<Int?> = mutableStateOf(null)
    val userId: State<Int?>
        get() = _userId

    private var _gameId: MutableState<Int?> = mutableStateOf(null)
    val gameId: State<Int?>
        get() = _gameId

    private var _game: MutableState<Game?> = mutableStateOf(null)
    val game: State<Game?>
        get() = _game

    private val _myBoardDisplayed: MutableState<Boolean> = mutableStateOf(true)
    val myBoardDisplayed: State<Boolean>
        get() = _myBoardDisplayed

    private var _player: MutableState<Player?> = mutableStateOf(null)
    var player: State<Player?>
        get() = _player

    fun startGame() {
        viewModelScope.launch {
            gameService.startNewGame(token) // requests to start a new game
            _gameId.value = gameService.getGameId(token) // asserts gameId
            assertGame() // asserts game
        }
    }

    fun assertGame() {
        val gameId = _gameId.value
        if (gameId != null) {
            viewModelScope.launch {
                _game.value = gameService.getGame(token, gameId)
                assertPlayer() // asserts player
            }
        }
    }

    private fun assertPlayer() {
        val gameInternal = game.value ?: return
        val userId = _userId.value ?: return
        if (gameInternal.player1 == userId) {
            _player.value = Player.PLAYER1
        }
        _player.value = Player.PLAYER2
    }

    fun placeShip(shipType: ShipType, coordinate: Coordinate, orientation: Orientation) {
        val gameInternal = game.value ?: return
        val player = player.value ?: return
        if (gameInternal.state === GameState.FLEET_SETUP) {
            val playerBoard = getMyBoard(gameInternal)
            if (!playerBoard.isConfirmed()) {
                gameInternal.placeShip(shipType, coordinate, orientation, player)
                playerGame.tryPlaceShip(shipType, coordinate, orientation)
                    ?: return // tests if the action is valid
                viewModelScope.launch {
                    gameService.placeShip(token, shipType, coordinate, orientation)
                    _game.value = gameService.getGameState(token)
                }
            }
        }
    }

    fun moveShip(origin: Coordinate, destination: Coordinate) {
        val gameInternal = game.value ?: return
        val playerBoard = getMyBoard(gameInternal)
        if (gameInternal.state === GameState.FLEET_SETUP) {
            val playerGame = gameInternal.player1Game
            if (playerGame is PlayerPreparationPhase) {
                playerGame.tryMoveShip(origin, destination)
                    ?: return // tests if the action is valid
                viewModelScope.launch {
                    gameService.moveShip(token, origin, destination)
                    _game.value = gameService.getGameState(token)
                }
            }
        }
    }

    fun rotateShip(position: Coordinate) {
        val gameInternal = game.value ?: return
        if (gameInternal is SinglePhase) {
            val playerGame = gameInternal.player1Game
            if (playerGame is PlayerPreparationPhase) {
                playerGame.tryRotateShip(position)
                    ?: return // tests if the action is valid
                viewModelScope.launch {
                    gameService.rotateShip(token, position)
                    _game.value = gameService.getGameState(token) ?: _game.value
                }
            }
        }
    }

    fun confirmFleet() {
        val gameInternal = game.value ?: return
        if (gameInternal is SinglePhase) {
            val playerGame = gameInternal.player1Game
            if (playerGame is PlayerPreparationPhase) {
                playerGame.confirmFleet()
                viewModelScope.launch {
                    gameService.confirmFleet(token)
                    _game.value = gameService.getGameState(token) ?: _game.value
                }
            }
        }
    }

    fun placeShot(coordinate: Coordinate) {
        val gameInternal = game.value ?: return
        if (gameInternal is BattlePhase) {
            gameInternal.tryPlaceShot(gameInternal.player1, coordinate) // tests if the action is valid
                ?: return
            viewModelScope.launch {
                gameService.placeShot(token, coordinate) // does the action
                _game.value = gameService.getGameState(token) ?: _game.value // updates the game
                while (true) {
                    delay(1000)
                    val gameFromServices = gameService.getGameState(token) ?: throw Exception("Game not found")
                    gameFromServices as? BattlePhase ?: throw Exception("Game is not in battle phase")
                    userId.value ?: throw Exception("User not found")
                    if (gameFromServices.playersTurn == userId.value) {
                        _game.value = gameFromServices
                        break
                    }
                }
            }
        }
    }

    private fun getMyBoard(game: Game) = if (myBoardDisplayed.value) game.board1 else game.board2
}