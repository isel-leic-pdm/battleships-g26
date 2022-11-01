package com.example.battleships

import android.app.Application
import com.example.battleships.game.services.BattleshipsService
import com.example.battleships.game.services.FakeBattleshipService
import com.example.battleships.game.services.RealBattleshipsService
import com.example.battleships.menu.FakeUserService
import com.example.battleships.menu.UserService
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import java.net.URL

const val TAG = "BattleshipsApp"

interface DependenciesContainer {
    val battleshipsService: BattleshipsService
    val userService: UserService
}

private val battleshipsAPIHome = URL("https://4216-2001-690-2008-df53-50eb-1e5c-b012-16d4.ngrok.io")

class BattleshipsApplication : DependenciesContainer, Application() {
    private val httpClient: OkHttpClient by lazy { OkHttpClient() }
    private val jsonEncoder: Gson by lazy { GsonBuilder().create() }

    override val battleshipsService: BattleshipsService by lazy {
        FakeBattleshipService()
        RealBattleshipsService(
            httpClient = httpClient,
            jsonEncoder = jsonEncoder,
        )
    }

    override val userService: UserService
        get() = FakeUserService()
}
