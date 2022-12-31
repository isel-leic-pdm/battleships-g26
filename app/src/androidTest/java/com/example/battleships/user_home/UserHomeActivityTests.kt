package com.example.battleships.user_home

import android.content.Intent
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.lifecycle.Lifecycle
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.battleships.home.HomeActivity
import com.example.battleships.home.NavigateToGameTestTag
import com.example.battleships.home.NavigateToRankingsButtonTestTag
import com.example.battleships.testutils.createAndroidComposeRule
import com.example.battleships.ui.NavigateBackTestTag
import com.example.battleships.ui.NavigateToAppInfoTestTag
import com.example.battleships.ui.NavigateToUserInfoButton
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class UserHomeActivityTests {

    private val intent = Intent(
        InstrumentationRegistry.getInstrumentation().targetContext,
        HomeActivity::class.java
    ).also {
        it.putExtra(
            HomeActivity.HOME_TOKEN_EXTRA,
            "test token"
        )
    }

    @get:Rule
    val testRule = createAndroidComposeRule<HomeActivity>(intent)

    @Test
    fun screen_contains_all_options() {
        // Assert
        testRule.onNodeWithTag(NavigateToAppInfoTestTag).assertExists()
        testRule.onNodeWithTag(NavigateToRankingsButtonTestTag).assertExists()
        testRule.onNodeWithTag(NavigateToGameTestTag).assertExists()
        testRule.onNodeWithTag(NavigateToUserInfoButton).assertExists()
        testRule.onNodeWithTag(NavigateBackTestTag).assertDoesNotExist()
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
        testRule.onNodeWithTag(NavigateToAppInfoTestTag).performClick()
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