package com.example.battleships.info

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
class InfoActivityTests {

    @get:Rule
    val testRule = createAndroidComposeRule<InfoActivity>()

    @Test
    fun screen_only_contains_back_navigation_option() {

        // Assert
        testRule.onNodeWithTag(NavigateBackTestTag).assertExists()
        testRule.onNodeWithTag(NavigateToAppInfoTestTag).assertDoesNotExist()
    }

    @Test
    fun pressing_navigate_back_finishes_activity() {

        // Arrange
        testRule.onNodeWithTag("InfoScreen").assertExists()

        // Act
        testRule.onNodeWithTag(NavigateBackTestTag).performClick()
        testRule.waitForIdle()

        // Assert
        testRule.onNodeWithTag("InfoScreen").assertDoesNotExist()
        testRule.waitForIdle()
        assert(testRule.activityRule.scenario.state == Lifecycle.State.DESTROYED)
    }
}