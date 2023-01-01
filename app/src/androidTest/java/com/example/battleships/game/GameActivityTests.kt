package com.example.battleships.game

import android.content.Intent
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.battleships.testutils.createAndroidComposeRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class GameActivityTests {

    private val intent = Intent(
        InstrumentationRegistry.getInstrumentation().targetContext,
        GameActivity::class.java
    ).also {
        it.putExtra(
            GameActivity.TOKEN_EXTRA,
            ""
        )
    }

    @get:Rule
    val testRule = createAndroidComposeRule<GameActivity>(intent)

    @Test
    fun screen_contains_all_options() {
        testRule.onNodeWithTag("GameScreen").assertExists()

        testRule.onNodeWithTag(QuickGame, useUnmergedTree = true).assertExists()
        testRule.onNodeWithTag(NewGame, useUnmergedTree = true).assertExists()
        testRule.onNodeWithTag(RestoreGame, useUnmergedTree = true).assertExists()
    }
}