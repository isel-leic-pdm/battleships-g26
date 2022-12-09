package com.example.battleships.auth

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.battleships.DependenciesContainer
import com.example.battleships.ErrorMessage
import com.example.battleships.auth.views.LoadingState
import com.example.battleships.info.InfoActivity
import com.example.battleships.ui.NavigationHandlers
import com.example.battleships.user_home.UserHomeActivity

class AuthActivity : ComponentActivity() {

    companion object {
        fun navigate(origin: Activity) {
            with(origin) {
                val intent = Intent(this, AuthActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private val useCases by lazy {
        (application as DependenciesContainer).useCases
    }

    @Suppress("UNCHECKED_CAST")
    val vm by viewModels<AuthViewModel> {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return AuthViewModel(useCases) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val token = vm.token
            if (token != null) {
                if (token.isSuccess) {
                    val tokenResult = token.getOrNull() ?: return@setContent
                    if (tokenResult is AuthViewModel.Success) {
                        UserHomeActivity.navigate(this, tokenResult.token)
                    } else {
                        AuthScreen()
                    }
                }
                else AuthErrorMessage()
            }
            else {
                AuthScreen()
            }
        }
    }

    @Composable
    private fun AuthScreen() {
        val isCreateUserLoading =
            if (vm.isCreateUserLoading.value) LoadingState.Loading
            else LoadingState.Idle

        val isLoginLoading =
            if (vm.isLoginLoading.value) LoadingState.Loading
            else LoadingState.Idle

        LaunchScreen(
            isLogin = isLoginLoading,
            isRegister = isCreateUserLoading,
            onRegisterUser = { username, password -> vm.createUser(username, password) },
            onLoginUser = { username, password -> vm.login(username, password) },
            navigationHandlers = NavigationHandlers(
                onInfoRequested = { InfoActivity.navigate(this) },
                onBackRequested = { finish() }
            )
        )
    }

    @Composable
    private fun AuthErrorMessage() {
        ErrorMessage(
            onNonError = { vm.token?.getOrThrow() },
            onIoExceptionDismiss = { TODO("Not yet implemented") },
            onApiExceptionDismiss = { finishAndRemoveTask() }
        )
    }
}