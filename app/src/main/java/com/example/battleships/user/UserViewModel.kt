package com.example.battleships.user

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.battleships.rankings.UserStats
import com.example.battleships.use_cases.UseCases
import kotlinx.coroutines.launch

class UserViewModel(
    private val useCases: UseCases,
) : ViewModel() {
    private var _user by mutableStateOf<Result<UserStats>?>(null)
    val user: Result<UserStats>?
        get() = _user

    fun getUserById(id : Int){
        viewModelScope.launch {
            _user = try {
                Result.success(useCases.getUserById(id))
            }catch (e : Exception) {
                Log.e("UserViewModel", "Error loading user", e)
                Result.failure(e)
            }
        }
    }
}