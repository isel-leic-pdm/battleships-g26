package com.example.battleships.auth

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.battleships.auth.views.LoadingButton
import com.example.battleships.auth.views.LoadingState
import com.example.battleships.auth.views.PasswordOutlinedTextField
import com.example.battleships.home.CreateUserButtonTestTag
import com.example.battleships.ui.TopBar
import com.example.battleships.ui.theme.LightWhite

@Composable
internal fun LaunchScreen(
    isLogin: LoadingState,
    isRegister: LoadingState,
    onRegisterUser: (String, String) -> Unit,
    onLoginUser: (String, String) -> Unit,
) {
    val username = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        backgroundColor = MaterialTheme.colors.background,
        topBar = { TopBar() }
    ){
        paddingValues ->
        Column(
            verticalArrangement = Arrangement.SpaceAround,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
        ){
            Column(
                verticalArrangement = Arrangement.SpaceAround,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .offset(y = (-50).dp)
                    .size(350.dp)
                    .clip(RoundedCornerShape(30.dp))
                    .background(LightWhite)
            )
            {
                InputView(username, password, isLogin, isRegister, onRegisterUser, onLoginUser)
            }
        }
    }
}


enum class Action(val companion : String) {
    REGISTER("Register"),
    LOGIN("Log In");

    fun other() = when(this){
        REGISTER -> LOGIN
        LOGIN -> REGISTER
    }
}

@Composable
fun InputView(
    username: MutableState<String>,
    password: MutableState<String>,
    isLogin: LoadingState,
    isRegister: LoadingState,
    onRegister: (String, String) -> Unit,
    onLogin: (String, String) -> Unit,

    ) {
    val mContext = LocalContext.current
    val buttonAction = remember { mutableStateOf(Action.REGISTER) }
    val retypedPassword = remember { mutableStateOf("") }
    val passwordVisible = remember { mutableStateOf(false) }
    val retypePasswordVisible = remember { mutableStateOf(false) }

    OutlinedTextField(
        value = username.value,
        onValueChange = { username.value = it },
        label = { Text("Username") },
    )
    PasswordOutlinedTextField(label = "Password", password = password,
        passwordVisible = passwordVisible)

    if(buttonAction.value == Action.REGISTER) {
        PasswordOutlinedTextField(label = "Retype Password", password = retypedPassword,
            passwordVisible = retypePasswordVisible)
    }

    LoadingButton(
        value = buttonAction.value.companion,
        state = when(buttonAction.value) {
            Action.REGISTER -> isLogin
            Action.LOGIN -> isRegister
        },
        tagName = CreateUserButtonTestTag,
        onClick = {
            when(buttonAction.value) {
                Action.REGISTER -> {
                    if(retypedPassword.value != password.value)
                        Toast.makeText(mContext, "Passwords don't match", Toast.LENGTH_SHORT).show()
                    else if(username.value.isEmpty() || username.value.isBlank())
                        Toast.makeText(mContext, "Username is empty", Toast.LENGTH_SHORT).show()
                    else if(password.value.isEmpty() || password.value.isBlank())
                        Toast.makeText(mContext, "Password is empty", Toast.LENGTH_SHORT).show()
                    else onRegister(username.value, password.value)
                }
                Action.LOGIN -> onLogin(username.value, password.value)
            }
        }
    )
    Row {
        Text(text =
        when(buttonAction.value) {
            Action.REGISTER -> "Already have an account? "
            Action.LOGIN -> "Don't have an account? "
        })
        ClickableText(
            text = AnnotatedString(buttonAction.value.other().companion) ,
            onClick = {
                buttonAction.value = buttonAction.value.other()
            },
            style = TextStyle(Color.Blue)
        )
    }
}

@Preview
@Composable
fun LaunchScreenPreview(){
    LaunchScreen(LoadingState.Idle, LoadingState.Idle, { _, _ -> }, { _, _ ->})
}