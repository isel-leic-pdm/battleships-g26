package com.example.battleships

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import io.mockk.coEvery
import io.mockk.mockk

class BattleshipsTestApplication : DependenciesContainer, Application() {
    override var useCases: UseCases
        = mockk {
            coEvery {  }
        }
}

@Suppress("unused")
class BattleshipsTestRunner : AndroidJUnitRunner() {
    override fun newApplication(cl: ClassLoader, className: String, context: Context): Application {
        return super.newApplication(cl, BattleshipsTestApplication::class.java.name, context)
    }
}
