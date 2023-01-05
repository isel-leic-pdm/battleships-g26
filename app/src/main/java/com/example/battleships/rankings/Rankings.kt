package com.example.battleships.rankings

import com.example.battleships.dtos.RankingsDtoProperties
import com.example.battleships.dtos.UserStatsDtoProperties

data class UserRanking(val users: List<UserStats>)
data class UserStats(val id: Int, val username: String, val wins: Int, val gamesPlayed: Int)

fun rankings(rankings: RankingsDtoProperties) =
    UserRanking(rankings.users.map { UserStats(it.id, it.username, it.wins, it.gamesPlayed) })

fun userInfo(user : UserStatsDtoProperties): UserStats {
    return UserStats(user.id, user.username, user.wins, user.gamesPlayed)
}
