package com.example.battleships.game

import android.util.Log
import androidx.compose.runtime.*
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
    class GameResult(val game: Game, val player: Player)
    sealed class CreateGameResult
    class Success(val gameId: Int) : CreateGameResult()
    object Matchmaking : CreateGameResult()

    private var _game by mutableStateOf<Result<GameResult>?>(null)
    val userId: Result<GameResult>?
        get() = _game

    private var _createGameResult by mutableStateOf<Result<CreateGameResult>?>(null)
    val createGameResult: Result<CreateGameResult>?
        get() = _createGameResult

    fun startGame() {
        viewModelScope.launch {
            _createGameResult =
                try {
                    val gameId = useCases.createGame(token)
                    if (gameId == null) Result.success(Matchmaking)
                    else Result.success(Success(gameId))
                } catch (e: Exception) {
                    Log.e(TAG, "Error starting game", e)
                    Result.failure(e)
                }
        }
    }

    private fun updateGame() {
        viewModelScope.launch {
            _game = try {
                val res = useCases.fetchGame(token)
                Result.success(GameResult(res.first, res.second)).also { Log.i(TAG, "Game updated") }
            } catch (e: Exception) {
                Log.e(TAG, "Error getting game", e)
                Result.failure(e)
            }
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