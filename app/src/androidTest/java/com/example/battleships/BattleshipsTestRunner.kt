package com.example.battleships

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import io.mockk.coEvery
import io.mockk.mockk

class BattleshipsTestRunner : DependenciesContainer, Application() {
    override var useCases: UseCases =
        mockk {
            /*
            coEvery { fetchQuote() } returns
                Quote(text = "Test text", author = "Test author")

            coEvery { fetchWeekQuotes() } returns
                buildList {
                    for (count in 1..5) {
                        add(
                            Quote(
                                text = "Test text $count",
                                author = "Test author $count"
                            )
                        )
                    }
                }
             */
        }
}

@Suppress("unused")
class QuoteOfDayTestRunner : AndroidJUnitRunner() {
    override fun newApplication(cl: ClassLoader, className: String, context: Context): Application {
        return super.newApplication(cl, BattleshipsApplication::class.java.name, context)
    }
}
