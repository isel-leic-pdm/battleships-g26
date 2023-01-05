package com.example.battleships.services

import com.example.battleships.info.ServerInfo
import com.example.battleships.rankings.UserRanking
import com.example.battleships.rankings.UserStats

/**
 * This interface is responsible for providing the options that interact with the home.
 */
interface HomeDataServices {
    suspend fun getRankings(mode: Mode): UserRanking
    suspend fun getServerInfo(mode: Mode): ServerInfo
    suspend fun getUserById(id: Int, mode : Mode): UserStats
}