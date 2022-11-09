package com.example.battleships.services

import com.example.battleships.info.ServerInfo
import com.example.battleships.rankings.GameRanking

/**
 * This interface is responsible for providing the options that interact with the home.
 */
interface HomeDataServices {
    suspend fun getRankings(mode: Mode): GameRanking
    suspend fun getServerInfo(mode: Mode): ServerInfo
}