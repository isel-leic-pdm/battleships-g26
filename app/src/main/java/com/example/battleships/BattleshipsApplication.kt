package com.example.battleships

import android.app.Application
import com.example.battleships.game.BattleshipsService
import com.example.battleships.game.FakeBattleshipService
import com.example.battleships.menu.FakeUserService
import com.example.battleships.menu.UserService

const val TAG = "BattleshipsApp"

interface DependenciesContainer {
    val battleshipsService: BattleshipsService
    val userService: UserService
}

class BattleshipsApplication : DependenciesContainer, Application() {
    override val battleshipsService: BattleshipsService
        get() = FakeBattleshipService()
    override val userService: UserService
        get() = FakeUserService()
}
