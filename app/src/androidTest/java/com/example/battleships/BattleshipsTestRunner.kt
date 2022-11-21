package com.example.battleships

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import com.example.battleships.services.fake.FakeGameDataServices
import com.example.battleships.services.fake.FakeHomeDataServices
import com.example.battleships.services.fake.FakeUserDataServices
import com.example.battleships.services.real.RealGamesDataServices
import com.example.battleships.services.real.RealHomeDataServices
import com.example.battleships.services.real.RealUserDataServices
import io.mockk.coEvery
import io.mockk.mockk

class BattleshipsTestRunner : DependenciesContainer, Application() {
    override var useCases: UseCases
        = mockk {
            coEvery {  }
        }
}

@Suppress("unused")
class QuoteOfDayTestRunner : AndroidJUnitRunner() {
    override fun newApplication(cl: ClassLoader, className: String, context: Context): Application {
        return super.newApplication(cl, BattleshipsApplication::class.java.name, context)
    }
}
