package com.example.battleships.home

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.lifecycle.Lifecycle
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.battleships.ui.NavigateBackTestTag
import com.example.battleships.ui.NavigateToInfoTestTag
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class HomeActivityTests {

    @get:Rule
    val testRule = createAndroidComposeRule<HomeActivity>()

    @Test
    fun screen_contains_three_options() {
        testRule.onNodeWithTag(NavigateToInfoTestTag).assertExists()
        testRule.onNodeWithTag(NavigateToAuthenticationButtonTestTag).assertExists()
        testRule.onNodeWithTag(NavigateToRankingsButtonTestTag).assertExists()
        testRule.onNodeWithTag(NavigateBackTestTag).assertDoesNotExist()
    }

    @Test
    fun pressing_navigate_to_authentication_opens_authentication_activity() {
        // Arrange
        testRule.onNodeWithTag("HomeScreen").assertExists()
        testRule.onNodeWithTag("AuthScreen").assertDoesNotExist()

        // Act
        testRule.onNodeWithTag(NavigateToAuthenticationButtonTestTag).performClick()
        testRule.waitForIdle()

        // Assert
        testRule.onNodeWithTag("AuthScreen").assertExists()
        testRule.waitForIdle()
        assert(testRule.activityRule.scenario.state == Lifecycle.State.STARTED)
    }

    @Test
    fun pressing_navigate_to_rankings_opens_rankings_activity() {
        // Arrange
        testRule.onNodeWithTag("HomeScreen").assertExists()
        testRule.onNodeWithTag("RankingsScreen").assertDoesNotExist()

        // Act
        testRule.onNodeWithTag(NavigateToRankingsButtonTestTag).performClick()
        testRule.waitForIdle()

        // Assert
        testRule.onNodeWithTag("RankingsScreen").assertExists()
        testRule.waitForIdle()
        assert(testRule.activityRule.scenario.state == Lifecycle.State.STARTED)
    }

    @Test
    fun pressing_navigate_to_info_opens_info_activity() {
        // Arrange
        testRule.onNodeWithTag("HomeScreen").assertExists()
        testRule.onNodeWithTag("InfoScreen").assertDoesNotExist()

        // Act
        testRule.onNodeWithTag(NavigateToInfoTestTag).performClick()
        testRule.waitForIdle()

        // Assert
        testRule.onNodeWithTag("InfoScreen").assertExists()
        testRule.waitForIdle()
        assert(testRule.activityRule.scenario.state == Lifecycle.State.STARTED)
    }
}