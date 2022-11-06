package com.example.battleships.info

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch


class InfoViewModel(
    private val dataService: UserService
) : ViewModel() {
    private val rankings: MutableState<List?> = mutableStateOf(null)
    val token: State<String?>
        get() = _token

    fun fetchRankingList(username: String, password: String) {
        viewModelScope.launch {
            _isCreateUserLoading.value = true
            dataService.createUser(username, password)
            _isCreateUserLoading.value = false
        }
    }
}