package com.example.battleships.rankings

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.lifecycle.Lifecycle
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.battleships.ui.NavigateBackTestTag
import com.example.battleships.ui.NavigateToAppInfoTestTag
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class RankingsActivityTests {

    @get:Rule
    val testRule = createAndroidComposeRule<RankingsActivity>()

    @Test
    fun screen_contains_all_options() {
        // Assert
        testRule.onNodeWithTag(RankingListTestTag).assertExists()
        testRule.onNodeWithTag(NavigateBackTestTag).assertExists()
    }

    @Test
    fun pressing_navigate_back_finishes_activity() {
        // Arrange
        testRule.onNodeWithTag("RankingsScreen").assertExists()

        // Act
        testRule.onNodeWithTag(NavigateBackTestTag).performClick()
        testRule.waitForIdle()

        // Assert
        testRule.onNodeWithTag("AuthScreen").assertDoesNotExist()
        testRule.waitForIdle()
        assert(testRule.activityRule.scenario.state == Lifecycle.State.DESTROYED)
    }

    @Test
    fun pressing_refresh_button() {
        // Arrange
        testRule.onNodeWithTag("RankingsScreen").assertExists()
        testRule.onNodeWithTag(RankingListTestTag).assertExists()

        // Act
        testRule.onNodeWithTag(RefreshButtonTestTag).performClick()
        testRule.waitForIdle()

        // Assert
        testRule.onNodeWithTag("RankingsScreen").assertExists()
        testRule.onNodeWithTag(RankingListTestTag).assertExists()
    }
}