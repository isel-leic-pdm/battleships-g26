package com.example.battleships

import android.app.Application
import com.example.battleships.services.fake.FakeGameDataServices
import com.example.battleships.services.fake.FakeHomeDataServices
import com.example.battleships.services.fake.FakeUserDataServices
import com.example.battleships.services.real.RealGamesDataServices
import com.example.battleships.services.real.RealHomeDataServices
import com.example.battleships.services.real.RealUserDataServices
import com.example.battleships.use_cases.FakeUseCases
import com.example.battleships.use_cases.RealUseCases
import com.example.battleships.use_cases.UseCases
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import java.net.URL

const val TAG = "BattleshipsApp"

interface DependenciesContainer {
    val useCases: UseCases
}

const val URL = "https://cbf1-95-94-100-26.eu.ngrok.io"

private val battleshipsAPIHome = URL(URL)

class BattleshipsApplication : DependenciesContainer, Application() {
    private val httpClient: OkHttpClient by lazy { OkHttpClient() }
    private val jsonEncoder: Gson by lazy { GsonBuilder().create() }

    private val cases = Pair(
        RealUseCases(
            RealHomeDataServices(battleshipsAPIHome, httpClient, jsonEncoder),
            RealUserDataServices(httpClient, jsonEncoder),
            RealGamesDataServices(httpClient, jsonEncoder)
        ),
        FakeUseCases(
            FakeHomeDataServices(),
            FakeUserDataServices(),
            FakeGameDataServices()
        )
    )

    override val useCases: UseCases

    get() = cases.first

}
