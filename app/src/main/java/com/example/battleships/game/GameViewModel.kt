package com.example.battleships.game

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.battleships.game.domain.game.Game
import com.example.battleships.game.domain.game.GameState
import com.example.battleships.use_cases.UseCases
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
    sealed class GameResult
    data class Started(val gameResultInternal: GameResultInternal) : GameResult()
    object WaitingForOpponent : GameResult()
    object Matchmaking : GameResult()

    data class GameResultInternal(val game: Game, val player: Player)

    private var _game by mutableStateOf<Result<GameResult>?>(null)
    val game: Result<GameResult>?
        get() = _game

    fun startGame() {
        viewModelScope.launch {
            try {
                val result = useCases.createGame(token)
                if (result) {
                    Result.success(Matchmaking)
                    fetchWhileMatchmaking()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error starting game", e)
                _game = Result.failure(e)
            }
        }
    }

    private fun fetchWhileMatchmaking() {
        viewModelScope.launch {
            while (true) {
                try {
                    val res = useCases.fetchCurrentGameId(token)
                    if (res != null) {
                        _game = Result.success(WaitingForOpponent)
                        updateGame()
                        break
                    } else {
                        delay(1000)
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error getting game", e)
                    _game = Result.failure(e)
                    break
                }
            }
        }
    }

    private fun updateGame() {
        viewModelScope.launch {
            _game = try {
                val res = useCases.fetchGame(token)
                if (res != null) {
                    val (game, player) = res
                    Result.success(Started(GameResultInternal(game, player)))
                } else null
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
                val game = getGameIfStartedOrNull()?.game ?: return@launch
                if (game.state === GameState.BATTLE) break
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

    internal fun setGame(game: Game, player: Player) {
        _game = Result.success(Started(GameResultInternal(game, player)))
    }

    private fun getGameIfStartedOrNull(): GameResultInternal? {
        return game?.getOrNull()?.let {
            if (it is Started) it.gameResultInternal
            else null
        }
    }
}