package com.example.battleships.authentication

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.lifecycle.Lifecycle
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.battleships.UseCases
import com.example.battleships.auth.AuthActivity
import com.example.battleships.home.CreateUserButtonTestTag
import com.example.battleships.home.LoginButtonTestTag
import com.example.battleships.testutils.PreserveDefaultDependencies
import com.example.battleships.ui.NavigateBackTestTag
import com.example.battleships.ui.NavigateToInfoTestTag
import io.mockk.every
import io.mockk.mockk
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class AuthenticationActivityTests {

    @get:Rule
    val testRule = createAndroidComposeRule<AuthActivity>()

    @Test
    fun screen_contains_all_options() {
        // Assert
        testRule.onNodeWithTag(CreateUserButtonTestTag).assertExists()
        testRule.onNodeWithTag(LoginButtonTestTag).assertExists()
        testRule.onNodeWithTag(NavigateToInfoTestTag).assertExists()
        testRule.onNodeWithTag(NavigateBackTestTag).assertExists()
    }

    @Test
    fun pressing_navigate_back_finishes_activity() {
        // Arrange
        testRule.onNodeWithTag("AuthScreen").assertExists()

        // Act
        testRule.onNodeWithTag(NavigateBackTestTag).performClick()
        testRule.waitForIdle()

        // Assert
        testRule.onNodeWithTag("AuthScreen").assertDoesNotExist()
        testRule.waitForIdle()
        assert(testRule.activityRule.scenario.state == Lifecycle.State.DESTROYED)
    }

    @Test
    fun pressing_navigate_to_info_opens_info_screen() {
        // Arrange
        testRule.onNodeWithTag("AuthScreen").assertExists()

        // Act
        testRule.onNodeWithTag(NavigateToInfoTestTag).performClick()
        testRule.waitForIdle()

        // Assert
        testRule.onNodeWithTag("InfoScreen").assertExists()
        testRule.waitForIdle()
    }

    @Test
    fun pressing_login_button_opens_user_home_activity() {
        // Arrange
        testRule.onNodeWithTag("AuthScreen").assertExists()

        // Act
        testRule.onNodeWithTag(LoginButtonTestTag).performClick()
        testRule.waitForIdle()

        // Assert
        testRule.onNodeWithTag("UserHomeScreen").assertExists()
        testRule.waitForIdle()

        assert(testRule.activityRule.scenario.state == Lifecycle.State.STARTED)
    }
}