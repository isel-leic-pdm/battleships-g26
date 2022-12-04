package com.example.battleships.rankings

import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.battleships.services.UnexpectedResponseException
import com.example.battleships.testutils.PreserveDefaultFakeServiceRule
import com.example.battleships.testutils.createPreserveDefaultFakeServiceComposeRule
import io.mockk.coEvery
import io.mockk.mockk
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import pt.isel.battleships.R

@RunWith(AndroidJUnit4::class)
class RankingsActivityErrorTests {

    @get:Rule
    val testRule = createPreserveDefaultFakeServiceComposeRule()

    private val application by lazy {
        (testRule.activityRule as PreserveDefaultFakeServiceRule).testApplication
    }

    @Test
    fun error_displayed_when_fetching_rankings_throws_UnexpectedResponseException() {
        application.useCases = mockk {
            coEvery { fetchRankings() } throws UnexpectedResponseException()
        }

        ActivityScenario.launch(RankingsActivity::class.java).use {
            testRule.onNodeWithTag("QuoteView").assertDoesNotExist()
            testRule.onNodeWithTag("ErrorAlert").assertExists()
            val expected = application.resources.getString(R.string.error_exit_button_text)
            testRule.onNodeWithText(expected).assertExists()
        }
    }
}