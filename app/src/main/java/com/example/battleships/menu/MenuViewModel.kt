package com.example.battleships.menu

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch


class MenuViewModel(
    private val dataService: UserService
) : ViewModel() {
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
            dataService.createUser(username, password)
            _isCreateUserLoading.value = false
        }
    }

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _isLoginLoading.value = true
            _token.value = dataService.login(username, password)
            _isLoginLoading.value = false
        }
    }
}