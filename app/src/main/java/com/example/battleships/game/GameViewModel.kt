package com.example.battleships.game

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.battleships.game.BattleshipsService
import com.example.fleetbattletemp.game.domain.board.Coordinate
import com.example.fleetbattletemp.game.domain.game.Game
import com.example.fleetbattletemp.game.domain.ship.Orientation
import com.example.fleetbattletemp.game.domain.ship.ShipType
import kotlinx.coroutines.launch

/**
 * The ViewModel for BattleshipActivity.
 * This class holds the state of the game and provides the logic to interact with it.
 * Any actions that change the state of the game, will, first, be tested offline and, if valid, will
 * be sent to the server to be persisted. The server will then, test the action again and, if valid,
 * will return the new state of the game. This new state will be used to update the local state.
 */
class GameViewModel(
    private val dataService: BattleshipsService
) : ViewModel() {

    private val _game: MutableState<Game?> = mutableStateOf(null)
    val game: State<Game?>
        get() = _game

    fun startGame() {
        viewModelScope.launch {
            _game.value = dataService.getNewGame() ?: game.value
        }
    }

    fun placeShip(shipType: ShipType, coordinate: Coordinate, orientation: Orientation) {
        _game.value?.tryPlaceShip(shipType, coordinate, orientation)
            ?: return // tests if the action is valid
        viewModelScope.launch {
            _game.value = dataService.placeShip(_game.value!!, shipType, coordinate, orientation)
                ?: game.value
        }
    }

    fun moveShip(origin: Coordinate, destination: Coordinate) {
        _game.value?.tryMoveShip(origin, destination) ?: return
        viewModelScope.launch {
            _game.value = dataService.moveShip(_game.value!!, origin, destination) ?: game.value
        }
    }

    fun rotateShip(position: Coordinate) {
        _game.value?.tryRotateShip(position) ?: return
        viewModelScope.launch {
            _game.value = dataService.rotateShip(_game.value!!, position) ?: game.value
        }
    }

    fun confirmFleet() {
        _game.value?.tryConfirmFleet() ?: return
        viewModelScope.launch {
            _game.value = dataService.confirmFleet(_game.value!!) ?: game.value
        }
    }

    fun placeShot(coordinate: Coordinate) {
        _game.value?.tryPlaceShot(coordinate) ?: return
        viewModelScope.launch {
            _game.value = dataService.placeShot(_game.value!!, coordinate) ?: game.value
        }
    }
}