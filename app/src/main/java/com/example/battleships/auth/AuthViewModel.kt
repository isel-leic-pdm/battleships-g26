package com.example.battleships.auth

import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.battleships.services.Mode
import com.example.battleships.use_cases.UseCases
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AuthViewModel(private val useCases: UseCases): ViewModel() {
    private val TAG = "AuthViewModel"

    private var _userId by mutableStateOf<Result<Int>?>(null)
    val userId: Result<Int>?
        get() = _userId

    private var _token by mutableStateOf<Result<String>?>(null)
    val token: Result<String>?
        get() = _token

    private val _isCreateUserLoading: MutableState<Boolean> = mutableStateOf(false)
    val isCreateUserLoading: State<Boolean>
        get() = _isCreateUserLoading

    private val _isLoginLoading: MutableState<Boolean> = mutableStateOf(false)
    val isLoginLoading: State<Boolean>
        get() = _isLoginLoading

    fun createUser(username: String, password: String, login : Boolean = false, errorHandler : (Exception) -> Unit) {
        viewModelScope.launch {
            _isCreateUserLoading.value = true
            try {
                _userId = Result.success(useCases.createUser(username, password, Mode.FORCE_REMOTE))
                    .also { Log.i(TAG, "User created: $it") }
                _isCreateUserLoading.value = false
                if (login) {
                    login(username, password, errorHandler)
                }
            } catch (e: Exception) {
                errorHandler(e)
                Log.e(TAG, "Error creating user", e)
                _userId =Result.failure(e)
                _isCreateUserLoading.value = false
            }
        }
    }

    fun login(username: String, password: String, errorHandler : (Exception) -> Unit) {
        viewModelScope.launch {
            _isLoginLoading.value = true
            _token =
                try {
                    Result.success(useCases.createToken(username, password, Mode.FORCE_REMOTE)).also {
                        Log.i(TAG, "Token created: $it")
                    }
                } catch (e: Exception) {
                    errorHandler(e)
                    Log.e(TAG, "Error creating token", e)
                    Result.failure(e)
                }
            _isLoginLoading.value = false
        }
    }

    fun clearToken() { viewModelScope.launch { _token = null } }
}