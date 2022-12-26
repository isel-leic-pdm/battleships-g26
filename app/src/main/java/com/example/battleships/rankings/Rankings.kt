package com.example.battleships.rankings

import com.example.battleships.dtos.RankingsDtoProperties

data class GameRanking(val users: List<UserStats>)
data class UserStats(val id: Int, val username: String, val wins: Int, val gamesPlayed: Int)

fun rankings(rankings: RankingsDtoProperties) =
    GameRanking(rankings.users.map { UserStats(it.id, it.username, it.wins, it.gamesPlayed) })