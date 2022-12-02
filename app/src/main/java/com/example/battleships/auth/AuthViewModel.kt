package com.example.battleships.auth

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.battleships.UseCases
import com.example.battleships.services.Mode
import kotlinx.coroutines.launch


class AuthViewModel(private val useCases: UseCases): ViewModel() {
    private val _token: MutableState<String?> = mutableStateOf(null)
    val token: State<String?>
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
            createUser(username, password)
            useCases.createUser(username, password, Mode.FORCE_REMOTE)
            _isCreateUserLoading.value = false
        }
    }

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _isLoginLoading.value = true
            _token.value = useCases.createToken(username, password, Mode.FORCE_REMOTE)
            _isLoginLoading.value = false
        }
    }
}