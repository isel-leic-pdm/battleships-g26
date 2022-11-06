package com.example.battleships.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.battleships.menu.views.LoadingButton
import com.example.battleships.menu.views.LoadingState

@Composable
fun Menu(
    createLoading: LoadingState,
    loginLoading: LoadingState,
    onCreateUser: (String, String) -> Unit,
    onLogin: (String, String) -> Unit
) {
    Column {
        Spacer(modifier = Modifier.height(50.dp))
        CredentialsView(createLoading, onCreateUser, "Create User")
        Spacer(modifier = Modifier.height(30.dp))
        CredentialsView(loginLoading, onLogin, "Login")
    }
}

// Test tags for the TopBar navigation elements
const val CreateUserButtonTestTag = "CreateUserButton"
const val LoginButtonTestTag = "LoginUserButton"

@Composable
fun CredentialsView(
    createLoading: LoadingState,
    onCreate: (String, String) -> Unit,
    title: String
) {
    val username = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(title)
        TextField(
            value = username.value,
            onValueChange = { username.value = it },
            label = { Text("Username") }
        )
        TextField(
            value = password.value,
            onValueChange = { password.value = it },
            label = { Text("Password") }
        )
        LoadingButton(
            value = title,
            state = createLoading,
            tagName = if (title == "Create User") CreateUserButtonTestTag else LoginButtonTestTag,
            onClick = { onCreate(username.value, password.value) }
        )
    }
}

@Preview
@Composable
fun ComposableUserViewPreview() {
    fun onC(s1: String = "", s2: String = "") {}
    CredentialsView(
        createLoading = LoadingState.Loading,
        onCreate = ::onC,
        title = "LOGIN"
    )
}