package com.example.battleships.authentication

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.battleships.menu.MenuActivity
import com.example.battleships.home.CreateUserButtonTestTag
import com.example.battleships.home.LoginButtonTestTag
import com.example.battleships.ui.NavigateToInfoTestTag
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class AuthenticationActivityTests {

    @get:Rule
    val testRule = createAndroidComposeRule<MenuActivity>()

    @Test
    fun screen_only_contains_back_navigation_option() {

        // Assert
        testRule.onNodeWithTag(CreateUserButtonTestTag).assertDoesNotExist()
        testRule.onNodeWithTag(LoginButtonTestTag).assertDoesNotExist()
        testRule.onNodeWithTag(NavigateToInfoTestTag).assertDoesNotExist()
    }
}