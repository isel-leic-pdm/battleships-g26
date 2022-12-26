package com.example.battleships.auth

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.battleships.DependenciesContainer
import com.example.battleships.ErrorMessage
import com.example.battleships.auth.views.LoadingState
import com.example.battleships.home.HomeActivity
import com.example.battleships.ui.NavigationHandlers
import com.example.battleships.utils.ApiErrorHandler
import com.example.battleships.utils.getWith
import okhttp3.internal.wait

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
                val tokenResult = token.getWith(LocalContext.current)
                if (tokenResult != null)
                    HomeActivity.navigate(this, tokenResult)
                else AuthScreen(Action.LOGIN)
            } else AuthScreen()
        }
    }

    @Composable
    private fun AuthScreen(action: Action = Action.REGISTER) {
        val context = LocalContext.current
        val isCreateUserLoading =
            if (vm.isCreateUserLoading.value) LoadingState.Loading
            else LoadingState.Idle

        val isLoginLoading =
            if (vm.isLoginLoading.value) LoadingState.Loading
            else LoadingState.Idle

        LaunchScreen(
            isLogin = isLoginLoading,
            isRegister = isCreateUserLoading,
            onRegisterUser = { username, password ->
                vm.createUser(username, password, true, ApiErrorHandler(context))
            },
            onLoginUser = { username, password ->
                vm.login(username, password, ApiErrorHandler(context))
            },
            navigationHandlers = NavigationHandlers(
                onBackRequested = { finish() }
            ),
            action = action
        )
    }
}