package com.example.battleships.game

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.battleships.UseCases
import pt.isel.daw.dawbattleshipgame.domain.board.Coordinate
import com.example.battleships.game.domain.game.Game
import kotlinx.coroutines.launch
import pt.isel.daw.dawbattleshipgame.domain.player.Player
import pt.isel.daw.dawbattleshipgame.domain.ship.Orientation
import pt.isel.daw.dawbattleshipgame.domain.ship.ShipType

const val TAG = "GameViewModel"

/**
 * The ViewModel for BattleshipActivity.
 * This class holds the state of the game and provides the logic to interact with it.
 * Any actions that change the state of the game, will, first, be tested offline and, if valid, will
 * be sent to the server to be persisted. The server will then, test the action again and, if valid,
 * will return the new state of the game. This new state will be used to update the local state.
 */
class GameViewModel(
    private val useCases: UseCases,
    private val token: String
) : ViewModel() {
    private var _game: MutableState<Game?> = mutableStateOf(null)
    val game: State<Game?>
        get() = _game

    private val _myBoardDisplayed: MutableState<Boolean> = mutableStateOf(true)
    val myBoardDisplayed: State<Boolean>
        get() = _myBoardDisplayed

    var player: Player? = null

    fun startGame() {
        viewModelScope.launch {
            useCases.createGame(token) // requests to start a new game
            Log.i(TAG, "Game started")
            updateGame() // asserts game
        }
    }

    private fun updateGame() {
        viewModelScope.launch {
            val res = useCases.fetchGame(token)
            Log.i(TAG, "Game updated")
            if (res == null) {
                Log.e(TAG, "Tried to update game but it was null")
                return@launch
            }
            _game.value = res.first
            player = res.second
            Log.i(TAG, "Game updated")
        }
    }

    fun setFleet(ships: List<Triple<ShipType, Coordinate, Orientation>>) {
        viewModelScope.launch {
            val res = useCases.setFleet(token, ships)
            if (!res) {
                Log.e(TAG, "Tried to place ship but it was null")
                return@launch
            } else updateGame()
            Log.i(TAG, "Ship placed")
        }
    }

    fun moveShip(origin: Coordinate, destination: Coordinate) {
        /*
        val gameInternal = game.value ?: return
        val gameId = _gameId.value ?: return
        if (gameInternal.state === GameState.FLEET_SETUP) {
            val playerBoard = getMyBoard(gameInternal)
            if (!playerBoard.isConfirmed()) {
                gameInternal.moveShip(origin, destination)
                    ?: return // tests if the action is valid
                viewModelScope.launch {
                    useCases.moveShip(token, gameId, origin, destination)
                    _game.value = useCases.getGame(token, gameId)
                }
            }
        }
         */
    }

    fun rotateShip(position: Coordinate) {
        /*
        val gameInternal = game.value ?: return
        val gameId = _gameId.value ?: return
        if (gameInternal.state === GameState.FLEET_SETUP) {
            val playerBoard = getMyBoard(gameInternal)
            if (!playerBoard.isConfirmed()) {
                gameInternal.rotateShip(position)
                    ?: return // tests if the action is valid
                viewModelScope.launch {
                    useCases.rotateShip(token, gameId, position)
                    _game.value = useCases.getGame(token, gameId) ?: _game.value
                }
            }
        }
         */
    }

    fun placeShot(coordinate: Coordinate) {
        /*
        val gameInternal = game.value ?: return
        val gameId = _gameId.value ?: return
        if (gameInternal.state === GameState.BATTLE) {
            gameInternal.placeShot(gameInternal.player1, coordinate) // tests if the action is valid
                ?: return
            viewModelScope.launch {
                useCases.placeShot(token, gameId, coordinate) // does the action
                _game.value = useCases.getGame(token, gameId) ?: _game.value // updates the game
                while (true) {
                    delay(1000)
                    val gameFromServices = useCases.getGame(token, gameId) ?: throw Exception("Game not found")
                    userId.value ?: throw Exception("User not found")
                    if (gameFromServices.playerTurn == userId.value) {
                        _game.value = gameFromServices
                        break
                    }
                }
            }
        }
         */
    }

    private fun getMyBoard(game: Game) = if (myBoardDisplayed.value) game.board1 else game.board2

    internal fun setGame(game: Game) {
        _game.value = game
    }
}