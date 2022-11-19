package com.example.battleships.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.battleships.auth.views.LoadingButton
import com.example.battleships.auth.views.LoadingState
import com.example.battleships.home.CreateUserButtonTestTag
import com.example.battleships.home.LoginButtonTestTag
import com.example.battleships.home.Menu
import com.example.battleships.ui.NavigationHandlers
import com.example.battleships.ui.TopBar
import com.example.battleships.ui.theme.BattleshipsTheme
import com.example.battleships.ui.theme.LightWhite

@Composable
internal fun AuthScreen(
    onBackRequested: () -> Unit = { },
    isCreated: LoadingState,
    isLogin: LoadingState,
    onCreateUser: (String, String) -> Unit,
    onLoginUser: (String, String) -> Unit,
) {
    BattleshipsTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            backgroundColor = MaterialTheme.colors.background,
            topBar = { TopBar() }
        ) { padding ->
            Column(
                verticalArrangement = Arrangement.SpaceAround,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
            ) {
                Menu(
                    isCreated,
                    isLogin,
                    onCreateUser,
                    onLoginUser,
                )
            }
        }
    }
}

@Composable
fun LaunchScreen() {
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
                OutlinedTextField(
                    value = username.value,
                    onValueChange = { username.value = it },
                    label = { Text("Username") }
                )
                OutlinedTextField(
                    value = password.value,
                    onValueChange = { password.value = it },
                    label = { Text("Password") }
                )
                LoadingButton(
                    value = "Register",
                    state = LoadingState.Idle,
                    tagName = CreateUserButtonTestTag,
                    onClick = {  }
                )
                Row {
                    Text(text = "Already have an account? ")
                    ClickableText(
                        text = AnnotatedString("Log In") ,
                        onClick = {},
                        style = TextStyle(Color.Blue)
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun LaunchScreenPreview(){
    LaunchScreen()
}


@Preview
@Composable
fun AuthScreenPreview(){
    AuthScreen(isCreated = LoadingState.Idle, isLogin = LoadingState.Idle,
        onCreateUser = { _, _ ->  } ,
        onLoginUser = { _, _ ->  }
    )
}