package com.example.battleships.game

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.battleships.game.domain.board.Coordinate
import com.example.battleships.game.domain.state.Game
import com.example.battleships.game.domain.state.SinglePhase
import com.example.battleships.game.domain.state.single.PlayerPreparationPhase
import com.example.battleships.game.domain.ship.Orientation
import com.example.battleships.game.domain.ship.ShipType
import kotlinx.coroutines.launch

/**
 * The ViewModel for BattleshipActivity.
 * This class holds the state of the game and provides the logic to interact with it.
 * Any actions that change the state of the game, will, first, be tested offline and, if valid, will
 * be sent to the server to be persisted. The server will then, test the action again and, if valid,
 * will return the new state of the game. This new state will be used to update the local state.
 */
class GameViewModel(
    private val gameService: BattleshipsService
) : ViewModel() {

    private val _game: MutableState<Game?> = mutableStateOf(null)
    val game: State<Game?>
        get() = _game

    fun startGame() {
        viewModelScope.launch {
            gameService.startNewGame()
            _game.value = gameService.getGameState()
        }
    }

    fun placeShip(shipType: ShipType, coordinate: Coordinate, orientation: Orientation) {
        val gameInternal = game.value ?: return
        if (gameInternal is SinglePhase) {
            val playerGame = gameInternal.player1Game
            if (playerGame is PlayerPreparationPhase) {
                playerGame.tryPlaceShip(shipType, coordinate, orientation)
                    ?: return // tests if the action is valid
                viewModelScope.launch {
                    gameService.placeShip(shipType, coordinate, orientation)
                    _game.value = gameService.getGameState()
                }
            }
        }
    }

    fun moveShip(origin: Coordinate, destination: Coordinate) {
        val gameInternal = game.value ?: return
        if (gameInternal is SinglePhase) {
            val playerGame = gameInternal.player1Game
            if (playerGame is PlayerPreparationPhase) {
                playerGame.tryMoveShip(origin, destination)
                    ?: return // tests if the action is valid
                viewModelScope.launch {
                    gameService.moveShip(origin, destination)
                    _game.value = gameService.getGameState()
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
                    gameService.rotateShip(position)
                    _game.value = gameService.getGameState()
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
                    gameService.confirmFleet()
                    _game.value = gameService.getGameState()
                }
            }
        }
    }

    fun placeShot(coordinate: Coordinate) {
        TODO("Not yet implemented")
    }
}