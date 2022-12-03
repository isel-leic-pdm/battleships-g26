package com.example.battleships.game

import android.content.Intent
import androidx.compose.ui.test.onNodeWithTag
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.battleships.services.fake.FakeGameDataServices
import com.example.battleships.services.fake.TIMEOUT
import com.example.battleships.testutils.PreserveDefaultFakeServiceRule
import com.example.battleships.testutils.createAndroidComposeRule
import com.example.battleships.testutils.createPreserveDefaultFakeServiceComposeRule
import com.example.battleships.user_home.UserHomeActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import pt.isel.daw.dawbattleshipgame.domain.game.Configuration
import pt.isel.daw.dawbattleshipgame.domain.ship.ShipType


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

        testRule.onNodeWithTag(CarrierShipButtonTestTag).assertExists()
        testRule.onNodeWithTag(BattleshipShipButtonTestTag).assertExists()
        testRule.onNodeWithTag(CruiserShipButtonTestTag).assertExists()
        testRule.onNodeWithTag(SubmarineShipButtonTestTag).assertExists()
        testRule.onNodeWithTag(DestroyerShipButtonTestTag).assertExists()

        testRule.onNodeWithTag(ConfirmFleetButtonTestTag).assertExists()
    }
}