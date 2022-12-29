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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.battleships.auth.views.LoadingButton
import com.example.battleships.auth.views.LoadingState
import com.example.battleships.auth.views.PasswordOutlinedTextField
import com.example.battleships.home.LoginButtonTestTag
import com.example.battleships.home.RegisterUserButtonTestTag
import com.example.battleships.ui.NavigationHandlers
import com.example.battleships.ui.TopBar
import com.example.battleships.ui.theme.BattleshipsTheme
import com.example.battleships.ui.theme.Milk
import pt.isel.battleships.R

// Test tags for the TopBar navigation elements
const val RegisterUserButtonTestTag = "CreateUserButton"
const val LoginButtonTestTag = "LoginUserButton"
const val SwitchToLoginButtonTestTag = "SwitchToLoginButton"
const val SwitchToRegisterButtonTestTag = "SwitchToRegisterButton"

@Composable
internal fun LaunchScreen(
    isLogin: LoadingState,
    isRegister: LoadingState,
    onRegisterUser: (String, String) -> Unit,
    onLoginUser: (String, String) -> Unit,
    navigationHandlers: NavigationHandlers? = null,
    action: Action
) {
    val username = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    BattleshipsTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize().testTag("AuthScreen"),
            backgroundColor = MaterialTheme.colors.background,
            topBar = { TopBar(
                title = stringResource(id = R.string.auth_screen_top_app_bar_title),
                navigation = navigationHandlers ?: NavigationHandlers()
            )}
        ){ paddingValues ->
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
                        .background(Milk)
                )
                {
                    InputView(username, password, isLogin,
                        isRegister, onRegisterUser, onLoginUser,
                        action
                    )
                }
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
    action: Action
) {
    val mContext = LocalContext.current
    val buttonAction = remember { mutableStateOf(action) }
    val retypedPassword = remember { mutableStateOf("") }
    val passwordVisible = remember { mutableStateOf(false) }
    val retypePasswordVisible = remember { mutableStateOf(false) }

    OutlinedTextField(
        value = username.value,
        onValueChange = { username.value = it },
        label = { Text(stringResource(id = R.string.username_lable)) },
    )
    PasswordOutlinedTextField(label = stringResource(id = R.string.password_lable_1), password = password,
        passwordVisible = passwordVisible)

    if(buttonAction.value == Action.REGISTER) {
        PasswordOutlinedTextField(label = stringResource(id = R.string.password_lable_2), password = retypedPassword,
            passwordVisible = retypePasswordVisible)
    }

    val confirmOperationButtonTestTag = when (buttonAction.value) {
        Action.REGISTER -> RegisterUserButtonTestTag
        Action.LOGIN -> LoginButtonTestTag
    }

    val passwordDoNotMatchError = stringResource(id = R.string.password_dont_match_error)
    val usernameIsEmptyError = stringResource(id = R.string.username_is_empty_error)
    val passwordIsEmptyError = stringResource(id = R.string.password_is_empty_error)

    val loadingButtonText = when (buttonAction.value) {
        Action.REGISTER -> stringResource(id = R.string.auth_screen_register_action)
        Action.LOGIN -> stringResource(id = R.string.auth_screen_login_action)
    }

    LoadingButton(
        value = loadingButtonText,
        state = when(buttonAction.value) {
            Action.REGISTER -> isLogin
            Action.LOGIN -> isRegister
        },
        tagName = confirmOperationButtonTestTag,
        onClick = {
            when(buttonAction.value) {
                Action.REGISTER -> {
                    if(retypedPassword.value != password.value)
                        Toast.makeText(mContext, passwordDoNotMatchError, Toast.LENGTH_SHORT).show()
                    else if(username.value.isEmpty() || username.value.isBlank())
                        Toast.makeText(mContext, usernameIsEmptyError, Toast.LENGTH_SHORT).show()
                    else if(password.value.isEmpty() || password.value.isBlank())
                        Toast.makeText(mContext, passwordIsEmptyError, Toast.LENGTH_SHORT).show()
                    else onRegister(username.value, password.value)
                }
                Action.LOGIN -> onLogin(username.value, password.value)
            }
        }
    )

    val switchButtonTestTag = when (buttonAction.value) {
        Action.REGISTER -> SwitchToLoginButtonTestTag
        Action.LOGIN -> SwitchToRegisterButtonTestTag
    }

    val switchActionTest = when (buttonAction.value.other()) {
        Action.REGISTER -> stringResource(id = R.string.auth_screen_register_action)
        Action.LOGIN -> stringResource(id = R.string.auth_screen_login_action)
    }
    Row {
        Text(text =
        when(buttonAction.value) {
            Action.REGISTER -> stringResource(id = R.string.auth_screen_already_have_account)
            Action.LOGIN -> stringResource(id = R.string.auth_screen_dont_have_account)
        })
        ClickableText(
            modifier = Modifier.testTag(switchButtonTestTag),
            text = AnnotatedString(switchActionTest),
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
    LaunchScreen(LoadingState.Idle, LoadingState.Idle, { _, _ -> }, { _, _ ->}, null, Action.REGISTER)
}