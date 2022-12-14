package com.example.battleships.auth

import android.util.Log
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.battleships.services.Mode
import com.example.battleships.use_cases.UseCases
import kotlinx.coroutines.launch
import kotlin.math.log

class AuthViewModel(private val useCases: UseCases): ViewModel() {
    sealed class TokenResult
    object InvalidCredentials: TokenResult()
    data class Success(val token: String): TokenResult()

    private var _userId by mutableStateOf<Result<Int>?>(null)
    private val userId: Result<Int>?
        get() = _userId

    private var _token by mutableStateOf<Result<TokenResult>?>(null)
    val token: Result<TokenResult>?
        get() = _token

    private val _isCreateUserLoading: MutableState<Boolean> = mutableStateOf(false)
    val isCreateUserLoading: State<Boolean>
        get() = _isCreateUserLoading

    private val _isLoginLoading: MutableState<Boolean> = mutableStateOf(false)
    val isLoginLoading: State<Boolean>
        get() = _isLoginLoading

    fun createUser(username: String, password: String) {
        viewModelScope.launch {
            _isCreateUserLoading.value = true
            _userId =
                try {
                    Result.success(useCases.createUser(username, password, Mode.FORCE_REMOTE))
                        .also { Log.i("AuthViewModel", "User created: $it") }
                } catch (e: Exception) {
                    Log.e("AuthViewModel", "Error creating user", e)
                    Result.failure(e)
                }
            _isCreateUserLoading.value = false
            if(userId?.getOrNull() != null) login(username, password)
        }
    }

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _isLoginLoading.value = true
            _token =
                try {
                    val token = useCases.createToken(username, password, Mode.FORCE_REMOTE)
                    Result.success(
                        if (token == null) InvalidCredentials
                        else Success(token)
                    ).also { Log.i("AuthViewModel", "Token created: $it") }
                } catch (e: Exception) {
                    Log.e("AuthViewModel", "Error creating token", e)
                    Result.failure(e)
                }
            _isLoginLoading.value = false
        }
    }
}