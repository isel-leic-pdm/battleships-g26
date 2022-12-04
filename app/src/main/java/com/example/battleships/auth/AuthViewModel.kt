package com.example.battleships.auth

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.battleships.UseCases
import com.example.battleships.services.Mode
import kotlinx.coroutines.launch


class AuthViewModel(private val useCases: UseCases): ViewModel() {
    private var _userId by mutableStateOf<Result<Int>?>(null)
    val login: Result<Int>?
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

    fun createUser(username: String, password: String) {
        viewModelScope.launch {
            _isCreateUserLoading.value = true
            _userId =
                try {
                    Result.success(useCases.createUser(username, password, Mode.FORCE_REMOTE))
                } catch (e: Exception) { Result.failure(e) }
            _isCreateUserLoading.value = false
        }
    }

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _isLoginLoading.value = true
            _token =
                try {
                    Result.success(useCases.createToken(username, password, Mode.FORCE_REMOTE))
                } catch (e: Exception) { Result.failure(e) }
            _isLoginLoading.value = false
        }
    }
}