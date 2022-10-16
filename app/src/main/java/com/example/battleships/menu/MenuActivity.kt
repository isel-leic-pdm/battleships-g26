package com.example.battleships.menu

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.battleships.DependenciesContainer
import com.example.battleships.menu.views.LoadingState

class MenuActivity : ComponentActivity() {
    private val service by lazy {
        (application as DependenciesContainer).userService
    }

    @Suppress("UNCHECKED_CAST")
    val vm by viewModels<MenuViewModel> {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return MenuViewModel(service) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val isCreateUserLoading =
                if (vm.isCreateUserLoading.value) LoadingState.Loading
                else LoadingState.Idle

            val isLoginLoading =
                if (vm.isLoginLoading.value) LoadingState.Loading
                else LoadingState.Idle

            MenuScreen(
                onBackRequested = { finish() },
                isCreated = isCreateUserLoading,
                isLogin = isLoginLoading,
                onCreateUser = { username, password -> vm.createUser(username, password) },
                onLoginUser = { username, password -> vm.login(username, password) },
            )
            if (vm.user.value != null) {
                navigateToGameScreen(this)
            }
        }
    }
}