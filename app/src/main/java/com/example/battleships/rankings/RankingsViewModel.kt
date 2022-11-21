package com.example.battleships.rankings

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.battleships.UseCases
import com.example.battleships.services.Mode
import kotlinx.coroutines.launch


class RankingsViewModel(private val useCases: UseCases): ViewModel() {
    private val _rankings: MutableState<GameRanking> = mutableStateOf(GameRanking(emptyList()))
    val rankings: State<GameRanking>
        get() = _rankings

    fun loadRankings() {
        viewModelScope.launch {
            _rankings.value = useCases.rankings(Mode.FORCE_REMOTE)
        }
    }
}