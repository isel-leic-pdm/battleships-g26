package com.example.battleships.auth.views

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation

@Composable
fun PasswordOutlinedTextField(
    label : String,
    password : MutableState<String>,
    passwordVisible : MutableState<Boolean>
){
    OutlinedTextField(
        value = password.value,
        onValueChange = { password.value = it },
        label = { Text(label) },
        visualTransformation = if (passwordVisible.value)
            VisualTransformation.None else PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        trailingIcon = {
            val image = if (passwordVisible.value)
                Icons.Filled.Visibility
            else Icons.Filled.VisibilityOff
            val description = if (passwordVisible.value) "Hide password" else "Show password"
            IconButton(onClick = {passwordVisible.value = !passwordVisible.value}){
                Icon(imageVector = image, description)
            }
        }
    )
}



