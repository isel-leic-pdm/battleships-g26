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
    private val _user: MutableState<String?> = mutableStateOf(null)
    val user: State<String?>
        get() = _user

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
            if (dataService.login(username, password)) {
                _user.value = username
            }
            _isLoginLoading.value = false
        }
    }
}