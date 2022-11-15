package com.example.battleships.home

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
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

        // Assert
        testRule.onNodeWithTag(NavigateToAuthenticationButtonTestTag).assertExists()
        testRule.onNodeWithTag(NavigateToRankingsButtonTestTag).assertExists()
        testRule.onNodeWithTag(NavigateToInfoTestTag).assertExists()
    }
}