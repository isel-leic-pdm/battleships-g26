package com.example.battleships.home

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.battleships.use_cases.UseCases
import kotlinx.coroutines.launch

class HomeViewModel(val useCases: UseCases) : ViewModel() {
    val TAG = "HomeViewModel"

    private var _userHome by mutableStateOf<Result<UserHome>?>(null)
    val userHome: Result<UserHome>?
        get() = _userHome

    private var _home by mutableStateOf<Result<Home>?>(null)
    val home : Result<Home>?
        get() = _home

    fun getUserHome(token : String) {
        viewModelScope.launch {
            _userHome = try {
                Result.success(useCases.getUserHome(token)).also {
                    Log.i(TAG, "User home fetched successfully: $it")
                }
            } catch (e : Exception) {
                Log.e(TAG, "Error loading user home", e)
                Result.failure(e)
            }
        }
    }

    fun getHome() {
        viewModelScope.launch {
            _home = try {
                Result.success(useCases.getHome()).also {
                    Log.i(TAG, "Home fetched successfully: $it")
                }
            } catch (e : Exception) {
                Log.e(TAG, "Error loading home", e)
                Result.failure(e)
            }
        }
    }
}