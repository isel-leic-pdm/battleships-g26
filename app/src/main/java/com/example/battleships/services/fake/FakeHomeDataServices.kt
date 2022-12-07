package com.example.battleships.services.fake

import com.example.battleships.info.ServerAuthor
import com.example.battleships.info.ServerInfo
import com.example.battleships.rankings.GameRanking
import com.example.battleships.rankings.UserStats
import com.example.battleships.services.HomeDataServices
import com.example.battleships.services.Mode

class FakeHomeDataServices : HomeDataServices {
    override suspend fun getRankings(mode: Mode) = GameRanking(
            listOf(
                UserStats("user1", 10, 20),
                UserStats("user2", 5, 10),
                UserStats("user3", 2, 5),
                UserStats("user4", 1, 2),
                UserStats("user5", 0, 1)
            )
        )

    /*
    override suspend fun getRankings(mode: Mode): GameRanking {
        throw UnexpectedResponseException()
    }
     */

    override suspend fun getServerInfo(mode: Mode) = ServerInfo(
        listOf(
            ServerAuthor("Miguel Rocha", ""),
            ServerAuthor("António Carvalho", ""),
            ServerAuthor("Pedro Silva", ""),
        ),
        "1.0.0"
    )
}