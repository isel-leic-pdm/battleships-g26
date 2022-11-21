package com.example.battleships.auth

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.battleships.DependenciesContainer
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
            val isCreateUserLoading =
                if (vm.isCreateUserLoading.value) LoadingState.Loading
                else LoadingState.Idle

            val isLoginLoading =
                if (vm.isLoginLoading.value) LoadingState.Loading
                else LoadingState.Idle

            AuthScreen(
                isCreated = isCreateUserLoading,
                isLogin = isLoginLoading,
                onCreateUser = { username, password -> vm.createUser(username, password) },
                onLoginUser = { username, password -> vm.login(username, password) },
                navigationHandlers = NavigationHandlers(
                    onBackRequested = { finish() },
                    onInfoRequested = { InfoActivity.navigate(this) }
                )
            )
            val token = vm.token.value
            if (token != null) {
                UserHomeActivity.navigate(this, token)
            }
        }
    }
}