package com.example.battleships.home

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.battleships.use_cases.UseCases
import kotlinx.coroutines.launch

class UserHomeViewModel(val useCases: UseCases) : ViewModel() {
    private var _me by mutableStateOf<Result<UserHome>?>(null)
    val me: Result<UserHome>?
        get() = _me

    fun getUserHome(token : String) {
        viewModelScope.launch {
            _me = try {
                Result.success(useCases.getUserHome(token))
            } catch (e : Exception) {
                Log.e("UserHomeViewModel", "Error loading user", e)
                Result.failure(e)
            }
        }
    }
}