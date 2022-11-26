package com.example.battleships.user_home

import android.content.Intent
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.lifecycle.Lifecycle
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.battleships.testutils.createAndroidComposeRule
import com.example.battleships.ui.NavigateBackTestTag
import com.example.battleships.ui.NavigateToInfoTestTag
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class UserHomeActivityTests {

    private val intent = Intent(
        InstrumentationRegistry.getInstrumentation().targetContext,
        UserHomeActivity::class.java
    ).also {
        it.putExtra(
            UserHomeActivity.TOKEN_EXTRA,
            "test token"
        )
    }

    @get:Rule
    val testRule = createAndroidComposeRule<UserHomeActivity>(intent)

    @Test
    fun screen_contains_all_options() {
        // Assert
        testRule.onNodeWithTag(NavigateToInfoTestTag).assertExists()
        testRule.onNodeWithTag(NavigateBackTestTag).assertExists()
        testRule.onNodeWithTag("start-game").assertExists()
    }

    @Test
    fun pressing_navigate_back_finishes_activity() {
        // Arrange
        testRule.onNodeWithTag("UserHomeScreen").assertExists()

        // Act
        testRule.onNodeWithTag(NavigateBackTestTag).performClick()
        testRule.waitForIdle()

        // Assert
        testRule.onNodeWithTag("UserHomeScreen").assertDoesNotExist()
        testRule.waitForIdle()
        assert(testRule.activityRule.scenario.state == Lifecycle.State.DESTROYED)
    }

    @Test
    fun pressing_navigate_to_info_opens_info_screen() {
        // Arrange
        testRule.onNodeWithTag("UserHomeScreen").assertExists()

        // Act
        testRule.onNodeWithTag(NavigateToInfoTestTag).performClick()
        testRule.waitForIdle()

        // Assert
        testRule.onNodeWithTag("InfoScreen").assertExists()
        testRule.waitForIdle()
    }

    @Test
    fun pressing_start_game_opens_game_screen() {
        // Arrange
        testRule.onNodeWithTag("UserHomeScreen").assertExists()

        // Act
        testRule.onNodeWithTag("start-game").performClick()
        testRule.waitForIdle()

        // Assert
        testRule.onNodeWithTag("GameScreen").assertExists()
        testRule.waitForIdle()
        assert(testRule.activityRule.scenario.state == Lifecycle.State.STARTED)
    }
}