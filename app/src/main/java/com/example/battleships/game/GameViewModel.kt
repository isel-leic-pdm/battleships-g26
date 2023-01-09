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
import com.example.battleships.services.Mode
import com.example.battleships.utils.launchWithErrorHandling
import pt.isel.daw.dawbattleshipgame.domain.player.Player
import pt.isel.daw.dawbattleshipgame.domain.ship.Orientation
import pt.isel.daw.dawbattleshipgame.domain.ship.ShipType

private const val TAG = "GameViewModel"

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

    object Restoring : GameResult()

    /** The Game is in progress */
    data class Started(val gameResultInternal: GameResultInternal) : GameResult()
    data class GameResultInternal(val game: Game, val player: Player)

    private var _game by mutableStateOf<Result<GameResult>>(Result.success(NotCreated))
    val game: Result<GameResult>
        get() = _game

    fun checkIfUserIsInQueueOrInGame() {
        viewModelScope.launch {
            if (gameStarted()) return@launch
            _game = Result.success(Restoring)
            try {
                val result = useCases.checkIfUserIsInQueue(token, Mode.FORCE_REMOTE)
                if (result) {
                    _game = Result.success(Matchmaking)
                    return@launch
                }
                restoreGame()
            } catch (e: Exception) {
                Log.e(TAG, "checkIfUserIsInQueueOrInGame: ", e)
            }
        }
    }

    fun surrender() {
        viewModelScope.launch {
            val (game, _) = getGameAndPlayerIfStartedOrNull() ?: return@launch
            try {
                if (useCases.surrender(token, game.id, Mode.FORCE_REMOTE))
                    _game = Result.success(NotCreated)
            } catch (e: Exception) {
                Log.e(TAG, "surrender: ", e)
            }
        }
    }

    fun startGame(configuration: Configuration?, errorHandler: (Exception) -> Unit) {
        suspend fun gameCreatedHandler() {
            _game = Result.success(Matchmaking)
            keepOnUpdatingGameUntil { gameStarted() }
            Log.i(TAG, "Game started")
            keepOnUpdatingGameUntil { !isWaitingForOpponent() }
            Log.i(TAG, "Ready to play")
        }

        viewModelScope.launch {
            _game = Result.success(Creating)
            try {
                val result = useCases.createGame(token, configuration = configuration)
                if (result) {
                    Log.i(TAG, "Game created")
                    gameCreatedHandler()
                } else {
                    _game = Result.failure(Exception("Game already created"))
                }
            } catch (e: Exception) {
                val msg = e.message?.lowercase()
                Log.e(TAG, "Error creating game: $msg")
                if (msg != null && msg.contains("user is already in queue")) {
                    gameCreatedHandler()
                } else {
                    errorHandler(e)
                    Log.e(TAG, "Failed to start game")
                    _game = Result.failure(e)
                }
            }
        }
    }


    fun restoreGame() {
        fun notFoundHandler() {
            Log.e(TAG, "Could not restore game")
            _game = Result.success(NotCreated) // To display menu again
            return
        }

        viewModelScope.launch {
            _game = Result.success(Restoring)
            try {
                val res = useCases.fetchGame(token)
                if (res != null) {
                    val (game, player) = res
                    _game = Result.success(Started(GameResultInternal(game, player))).also {
                        Log.i(TAG, "Game restored")
                    }
                    keepOnUpdatingGameUntil { !isWaitingForOpponent() }
                } else {
                    notFoundHandler()
                }
            } catch (e: Exception) {
                notFoundHandler()
            }
        }
    }

    private suspend fun keepOnUpdatingGameUntil(stop: () -> Boolean) {
        while (!stop()) {
            updateGame()
            Log.e("FetchGame", "Fetching $stop")
            delay(3000)
        }
    }

    private suspend fun updateGame() {
        try {
            val res = useCases.fetchGame(token)
            if (res != null) {
                val (game, player) = res
                _game = Result.success(Started(GameResultInternal(game, player))).also {
                    Log.i(TAG, "Game updated")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error updating game", e)
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
            Log.i(TAG, "Fleet set")
            if (res) {
                keepOnUpdatingGameUntil { bothBoardsConfirmed() }
                Log.i(TAG, "Both boards confirmed")
                keepOnUpdatingGameUntil { !isWaitingForOpponent() }
                Log.i(TAG, "Ready to play")
            } else {
                Log.e(TAG, "Failed to set fleet")
            }
        }
    }

    fun placeShots(shots: ShotsList, errorHandler: (Exception) -> Unit) {
        if (isWaitingForOpponent()) {
            Log.i(TAG, "Waiting for opponent")
            return
        }

        viewModelScope.launchWithErrorHandling(errorHandler) {
            val success = useCases.placeShots(token, shots)
            if (success) {
                Log.i(TAG, "Shots placed")
                keepOnUpdatingGameUntil { isWaitingForOpponent() /* || !gameStarted()*/ }
                // if (changeToMenuIfGameDoNotExistAnymore()) return@launchWithErrorHandling
                keepOnUpdatingGameUntil { !isWaitingForOpponent() /* || !gameStarted()*/ }
                // if (changeToMenuIfGameDoNotExistAnymore()) return@launchWithErrorHandling
                Log.i(TAG, "Opponent place it's shots")
            }
        }
    }

    private fun changeToMenuIfGameDoNotExistAnymore(): Boolean {
        if (!gameStarted()) {
            _game = Result.success(NotCreated)
            return true
        }
        return false
    }

    internal fun setGame(game: Game, player: Player) {
        _game = Result.success(Started(GameResultInternal(game, player)))
    }


    private fun gameStarted(): Boolean {
        val gameAux = game.getOrNull()
        return gameAux != null && gameAux is Started
    }

    private fun getGameAndPlayerIfStartedOrNull(): Pair<Game, Player>? {
        val gameAux = game.getOrNull()
        if (gameAux == null || gameAux !is Started) return null
        return gameAux.gameResultInternal.game to gameAux.gameResultInternal.player
    }

    private fun isWaitingForOpponent(): Boolean {
        val (game, player) = getGameAndPlayerIfStartedOrNull() ?: return false
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

    private fun isFinished(): Boolean {
        val (game, _) = getGameAndPlayerIfStartedOrNull() ?: return false
        return game.state === GameState.FINISHED
    }

    private fun bothBoardsConfirmed(): Boolean {
        val (game, _) = getGameAndPlayerIfStartedOrNull() ?: return false
        return game.board1.isConfirmed() && game.board2.isConfirmed()
    }
}