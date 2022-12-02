package com.example.battleships.game

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.battleships.UseCases
import com.example.battleships.game.domain.game.Game
import com.example.battleships.game.domain.game.GameState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pt.isel.daw.dawbattleshipgame.domain.board.Coordinate
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

    /*
    private val _myBoardDisplayed: MutableState<Boolean> = mutableStateOf(true)
    val myBoardDisplayed: State<Boolean>
        get() = _myBoardDisplayed
     */

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
                Log.e(TAG, "Tried to place ships but an error occurred")
                return@launch
            }
            Log.i(TAG, "Ships placed, and confirmed")
            while (true) {
                updateGame()
                if (game.value?.state === GameState.BATTLE) break
                delay(1000)
                Log.d(TAG, "Waiting for opponent to place ships") // TODO: maybe, remove this later
            }
        }
    }

    fun placeShot(coordinate: Coordinate) {
        viewModelScope.launch {
            val res = useCases.placeShot(token, coordinate)
            if (!res) {
                Log.e(TAG, "Tried to place shot but an error occurred")
                return@launch
            } else updateGame()
            Log.i(TAG, "Shot placed")
        }
    }

    // private fun getMyBoard(game: Game) = if (myBoardDisplayed.value) game.board1 else game.board2

    internal fun setGame(game: Game) {
        _game.value = game
    }
}