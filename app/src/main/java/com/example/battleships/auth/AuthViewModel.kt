package com.example.battleships.auth

import android.content.Context
import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.battleships.services.Mode
import com.example.battleships.use_cases.UseCases
import com.example.battleships.utils.getWith
import kotlinx.coroutines.launch
import kotlin.math.log

class AuthViewModel(private val useCases: UseCases): ViewModel() {
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
            _userId =
                try {
                    Result.success(useCases.createUser(username, password, Mode.FORCE_REMOTE))
                        .also { Log.i("AuthViewModel", "User created: $it") }
                } catch (e: Exception) {
                    errorHandler(e)
                    Log.e("AuthViewModel", "Error creating user", e)
                    Result.failure(e)
                }
            _isCreateUserLoading.value = false
            if(login) login(username, password, errorHandler)
        }
    }

    fun login(username: String, password: String, errorHandler : (Exception) -> Unit) {
        viewModelScope.launch {
            _isLoginLoading.value = true
            _token =
                try {
                    Result.success(useCases.createToken(username, password, Mode.FORCE_REMOTE)).also {
                        Log.i("AuthViewModel", "Token created: $it")
                    }
                } catch (e: Exception) {
                    errorHandler(e)
                    Log.e("AuthViewModel", "Error creating token", e)
                    Result.failure(e)
                }
            _isLoginLoading.value = false
        }
    }
}