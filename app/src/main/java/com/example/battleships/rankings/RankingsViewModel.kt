package com.example.battleships.rankings

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.battleships.services.Mode
import com.example.battleships.use_cases.UseCases
import kotlinx.coroutines.launch

class RankingsViewModel(private val useCases: UseCases): ViewModel() {
    private var _isLoading by mutableStateOf(false)
    val isLoading: Boolean
        get() = _isLoading

    private var _rankings by mutableStateOf<Result<UserRanking>?>(null)
    val rankings: Result<UserRanking>?
        get() = _rankings

    fun loadRankings(forcedRefresh: Boolean = false) {
        viewModelScope.launch {
            _isLoading = true
            _rankings =
                try {
                    Result.success(useCases.fetchRankings(
                        if (forcedRefresh) Mode.FORCE_REMOTE
                        else Mode.AUTO
                    )).also { Log.d("RankingsViewModel", "Rankings loaded") }
                } catch (e: Exception) {
                    Log.e("RankingsViewModel", "Error loading rankings", e)
                    Result.failure(e)
                }
            _isLoading = false
        }
    }

}