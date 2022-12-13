package com.example.battleships.game

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.battleships.game.domain.game.Game
import com.example.battleships.game.domain.game.GameState
import com.example.battleships.services.Mode
import com.example.battleships.use_cases.UseCases
import kotlinx.coroutines.CoroutineScope
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

    /** The state of the Game View */
    sealed class GameResult

    /** The game is being initialized */
    object NotCreated : GameResult()

    /** The game is being created */
    object Creating : GameResult()

    /** The Game was created but didn't start yet */
    object Matchmaking : GameResult()

    /** The Game is in progress */
    data class Started(val gameResultInternal: GameResultInternal) : GameResult()
    data class GameResultInternal(val game: Game, val player: Player)

    private var _game by mutableStateOf<Result<GameResult>>(Result.success(NotCreated))
    val game: Result<GameResult>
        get() = _game

    fun startGame() {
        viewModelScope.launch {
            _game = Result.success(Creating)
            try {
                val result = useCases.createGame(token)
                if (result) {
                    _game = Result.success(Matchmaking)
                    keepOnFetchingGameUntil {
                        val gameAux = game.getOrNull()
                        gameAux != null && gameAux is Started
                    }
                }
                else {
                    _game = Result.failure(Exception("Game already created"))
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error starting game", e)
                _game = Result.failure(e)
            }
        }
    }

    fun restoreGame() {
        viewModelScope.launch {
            _game = Result.success(Matchmaking)
            keepOnFetchingGameUntil {
                val gameAux = game.getOrNull()
                gameAux != null && gameAux is Started
            }
        }
    }

    private fun keepOnFetchingGameUntil(stop: () -> Boolean) {
        viewModelScope.launch {
            while (!stop()) {
                updateGame()
                delay(1000)
            }
        }
    }

    private suspend fun updateGame() {
        try {
            val res = useCases.fetchGame(token)
            if (res != null) {
                val (game, player) = res
                _game = Result.success(Started(GameResultInternal(game, player)))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting game", e)
            _game = Result.failure(e)
        }
    }

    fun setFleet(ships: List<Triple<ShipType, Coordinate, Orientation>>) {
        val game = game.getOrNull() as? Started ?: return
        if (game.gameResultInternal.game.state != GameState.FLEET_SETUP) return
        viewModelScope.launch {
            try {
                val res = useCases.setFleet(token, ships)
                if (res) {
                    keepOnFetchingGameUntil { // updates game until player's fleet is confirmed
                        val gameAux = _game.getOrNull()
                        gameAux != null && gameAux is Started
                                && gameAux.gameResultInternal.game.getBoard(gameAux.gameResultInternal.player).isConfirmed()
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error setting fleet", e)
            }
        }
    }

    fun placeShot(coordinate: Coordinate) {
        val game = game.getOrNull() as? Started ?: return
        if (game.gameResultInternal.game.state != GameState.BATTLE) return
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
        return game.getOrNull()?.let {
            if (it is Started) it.gameResultInternal
            else null
        }
    }
}