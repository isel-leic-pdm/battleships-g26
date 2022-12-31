package com.example.battleships

import android.app.Application
import android.content.Context
import androidx.test.runner.AndroidJUnitRunner
import com.example.battleships.game.domain.game.ShotsList
import com.example.battleships.home.UserHome
import com.example.battleships.info.ServerInfo
import com.example.battleships.rankings.UserRanking
import com.example.battleships.rankings.UserStats
import com.example.battleships.use_cases.UseCases
import io.mockk.coEvery
import io.mockk.mockk

class BattleshipsTestApplication : DependenciesContainer, Application() {
    override var useCases: UseCases
        = mockk {
            coEvery { createUser("", "") } returns 1
            coEvery { createToken("", "")} returns "token"
            coEvery { createGame("token", configuration = null) } returns true
            coEvery { fetchGame("token") } returns null
            coEvery { fetchRankings() } returns UserRanking(emptyList())
            coEvery { setFleet("token", emptyList()) } returns true
            coEvery { placeShots("token", ShotsList(emptyList())) } returns true
            coEvery { fetchServerInfo() } returns ServerInfo(emptyList(), "1.0")
            coEvery { getUserById(1) } returns UserStats(1, "username", 0, 0)
            coEvery { getUserHome("token") } returns UserHome(1, "username")
    }
}

@Suppress("unused")
class BattleshipsTestRunner : AndroidJUnitRunner() {
    override fun newApplication(cl: ClassLoader, className: String, context: Context): Application {
        return super.newApplication(cl, BattleshipsTestApplication::class.java.name, context)
    }
}
