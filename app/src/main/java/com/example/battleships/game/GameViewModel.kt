package com.example.battleships.game

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.battleships.game.domain.board.Coordinate
import com.example.battleships.game.domain.game.Configuration
import com.example.battleships.game.domain.game.Game
import com.example.battleships.game.domain.game.GameState
import com.example.battleships.use_cases.UseCases
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.battleships.game.domain.game.ShotsList
import com.example.battleships.utils.launchWithErrorHandling
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

    fun startGame(configuration: Configuration? = null, errorHandler: (Exception) -> Unit) {
        viewModelScope.launch {
            _game = Result.success(Creating)
            try {
                val result = useCases.createGame(token, configuration = configuration)
                if (result) {
                    _game = Result.success(Matchmaking)
                    keepOnFetchingGameUntil { gameStarted() }
                    keepOnFetchingGameUntil { !isWaitingForOpponent() }
                } else {
                    _game = Result.failure(Exception("Game already created"))
                }
            } catch (e: Exception) {
                errorHandler(e)
                Log.e("Start Game", "Failed to start game")
                _game = Result.failure(e)
            }
        }
    }

    private fun isWaitingForOpponent(): Boolean {
        val gameAux = game.getOrNull()
        if (gameAux == null || gameAux !is Started) return false
        val game = gameAux.gameResultInternal.game
        val player = gameAux.gameResultInternal.player
        if (game.state === GameState.FLEET_SETUP) {
            return game.getBoard(player).isConfirmed() && !game.getBoard(player.other())
                .isConfirmed()
        }
        if (game.state === GameState.BATTLE) {
            val playerTurn = game.playerTurn ?: return false
            return game.getPlayerFromId(playerTurn) !== player
        }
        return false
    }

    private fun opponentConfirmBoard(): Boolean {
        val gameAux = game.getOrNull()
        if (gameAux == null || gameAux !is Started) return false
        val game = gameAux.gameResultInternal.game
        val player = gameAux.gameResultInternal.player
        if (game.state === GameState.FLEET_SETUP) {
            return game.getBoard(player.other()).isConfirmed()
        }
        if (game.state === GameState.BATTLE) {
            val playerTurn = game.playerTurn ?: return false
            return game.getPlayerFromId(playerTurn) !== player
        }
        return false
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
                delay(3000)
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

    fun setFleet(
        ships: List<Triple<ShipType, Coordinate, Orientation>>,
        errorHandler: (Exception) -> Unit
    ) {
        val game = game.getOrNull() as? Started ?: return
        if (game.gameResultInternal.game.state != GameState.FLEET_SETUP) return
        viewModelScope.launchWithErrorHandling(errorHandler) {
            val res = useCases.setFleet(token, ships)
            if (res) keepOnFetchingGameUntil { opponentConfirmBoard() }
        }
    }

    fun placeShots(shots: ShotsList, errorHandler: (Exception) -> Unit) {
        val game = game.getOrNull() as? Started ?: return
        if (game.gameResultInternal.game.state != GameState.BATTLE) return

        viewModelScope.launchWithErrorHandling(errorHandler) {
            if (game.gameResultInternal.player !=
                game.gameResultInternal.game.getPlayerFromId(
                    game.gameResultInternal.game.playerTurn!!
                )
            ) {
                keepOnFetchingGameUntil { !isWaitingForOpponent() }
            } else {
                val res = useCases.placeShots(token, shots)
                if (res) keepOnFetchingGameUntil {
                    isWaitingForOpponent()
                }
            }
        }
    }

    internal fun setGame(game: Game, player: Player) {
        _game = Result.success(Started(GameResultInternal(game, player)))
    }


    private fun gameStarted(): Boolean {
        val gameAux = game.getOrNull()
        return gameAux != null && gameAux is Started
    }
}