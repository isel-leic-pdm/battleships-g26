package com.example.battleships.services.fake

import com.example.battleships.info.ServerAuthor
import com.example.battleships.info.ServerInfo
import com.example.battleships.rankings.UserRanking
import com.example.battleships.rankings.UserStats
import com.example.battleships.services.HomeDataServices
import com.example.battleships.services.Mode

class FakeHomeDataServices : HomeDataServices {
    override suspend fun getRankings(mode: Mode) = UserRanking(
        listOf(
            UserStats(1, "user1", 10, 20),
            UserStats(2, "user2", 5, 10),
            UserStats(3, "user3", 2, 5),
            UserStats(4, "user4", 1, 2),
            UserStats(5, "user5", 0, 1)
        )
    )

    override suspend fun getServerInfo(mode: Mode) = ServerInfo(
        listOf(
            ServerAuthor("Miguel Rocha", ""),
            ServerAuthor("António Carvalho", ""),
            ServerAuthor("Pedro Silva", ""),
        ),
        "1.0.0"
    )

    override suspend fun getUserById(id: Int, mode: Mode): UserStats? {
        TODO("Not yet implemented")
    }
}