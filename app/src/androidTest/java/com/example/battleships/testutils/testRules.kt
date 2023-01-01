package com.example.battleships.testutils

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.platform.app.InstrumentationRegistry
import com.example.battleships.BattleshipsTestApplication
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

/**
 * Creates a test rule that starts an activity of type <A> with the received
 * intent.
 */
fun <A : ComponentActivity> createAndroidComposeRule(
    intent: Intent
): AndroidComposeTestRule<ActivityScenarioRule<A>, A> = AndroidComposeTestRule(
    activityRule = ActivityScenarioRule(intent),
    activityProvider = { rule ->
        var activity: A? = null
        rule.scenario.onActivity { activity = it }
        activity!!
    }
)

/**
 * Test rule that ensures that the default dependencies are preserved after the test is executed.
 *
 * Tests that make use of this rule are allowed to replace the globally accessible quote service test
 * double by another double that serves their purposes. The behaviour of the remaining tests
 * is preserved by saving the default fake and restoring it after the execution of the test.
 */
class PreserveDefaultFakeServiceRule : TestRule {

    val testApplication: BattleshipsTestApplication = InstrumentationRegistry
        .getInstrumentation()
        .targetContext
        .applicationContext as BattleshipsTestApplication

    override fun apply(test: Statement, description: Description): Statement =
        object : Statement() {
            override fun evaluate() {
                val defaultService = testApplication.useCases
                try {
                    test.evaluate()
                } finally {
                    testApplication.useCases = defaultService
                }
            }
        }
}

/**
 * Creates an empty compose rule that saves and restores the default fake quote service
 * implementation. An empty compose rule is useful when the Activity (the compose host)
 * is explicitly launched by the test itself.
 */
fun createPreserveDefaultFakeServiceComposeRule() =
    AndroidComposeTestRule<TestRule, ComponentActivity>(
        activityRule = PreserveDefaultFakeServiceRule(),
        activityProvider = {
            error("This rule does not provide an Activity. Launch and use the Activity yourself.")
        }
    )