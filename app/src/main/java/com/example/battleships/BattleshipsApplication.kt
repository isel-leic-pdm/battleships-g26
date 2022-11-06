package com.example.battleships

import android.app.Application
import com.example.battleships.rankings.services.RankingsService
import com.example.battleships.rankings.services.FakeRankingsService
import com.example.battleships.menu.FakeUserService
import com.example.battleships.menu.UserService
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import java.net.URL

const val TAG = "BattleshipsApp"

interface DependenciesContainer {
    val battleshipsService: RankingsService
    val userService: UserService
}

private val battleshipsAPIHome = URL("https://4216-2001-690-2008-df53-50eb-1e5c-b012-16d4.ngrok.io")

class BattleshipsApplication : DependenciesContainer, Application() {
    private val httpClient: OkHttpClient by lazy { OkHttpClient() }
    private val jsonEncoder: Gson by lazy { GsonBuilder().create() }

    override val battleshipsService: RankingsService by lazy {
        FakeRankingsService()
        /*
        RealBattleshipsService(
            httpClient = httpClient,
            jsonEncoder = jsonEncoder,
        )
         */
    }

    override val userService: UserService
        get() = FakeUserService()
}
