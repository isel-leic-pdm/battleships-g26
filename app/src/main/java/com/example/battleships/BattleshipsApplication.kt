package com.example.battleships

import android.app.Application
import com.example.battleships.services.fake.FakeGameDataServices
import com.example.battleships.services.fake.FakeHomeDataServices
import com.example.battleships.services.fake.FakeUserDataServices
import com.example.battleships.services.real.RealGamesDataServices
import com.example.battleships.services.real.RealHomeDataServices
import com.example.battleships.services.real.RealUserDataServices
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import java.net.URL

const val TAG = "BattleshipsApp"

interface DependenciesContainer {
    val useCases: UseCases
}

private val battleshipsAPIHome = URL("https://4216-2001-690-2008-df53-50eb-1e5c-b012-16d4.ngrok.io")

class BattleshipsApplication : DependenciesContainer, Application() {
    private val httpClient: OkHttpClient by lazy { OkHttpClient() }
    private val jsonEncoder: Gson by lazy { GsonBuilder().create() }

    override val useCases: UseCases
    /*
        get() = UseCases(
            RealHomeDataServices(battleshipsAPIHome, httpClient, jsonEncoder),
            RealUserDataServices(httpClient, jsonEncoder),
            RealGamesDataServices(httpClient, jsonEncoder)
        )
     */
        get() = UseCases(
            FakeHomeDataServices(),
            FakeUserDataServices(),
            FakeGameDataServices()
        )
}
