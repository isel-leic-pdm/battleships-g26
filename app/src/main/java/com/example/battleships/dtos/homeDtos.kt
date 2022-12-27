
package com.example.battleships.dtos

import com.example.battleships.utils.hypermedia.SirenEntity


/*--------------------------------HOME---------------------------------------------- */
data class HomeDtoProperties(val title: String)

typealias HomeDto = SirenEntity<HomeDtoProperties>
val HomeDtoType = SirenEntity.getType<HomeDtoProperties>()

/*--------------------------------RANKINGS---------------------------------------------- */
data class RankingsDtoProperties(val users: List<UserStatsDtoProperties>)

typealias RankingsDto = SirenEntity<RankingsDtoProperties>
val RankingsDtoType = SirenEntity.getType<RankingsDtoProperties>()

data class UserStatsDtoProperties(val id : Int, val username: String, val wins: Int, val gamesPlayed: Int)

typealias UserStatsDto = SirenEntity<UserStatsDtoProperties>
val UserStatsDtoType = SirenEntity.getType<UserStatsDtoProperties>()


/*--------------------------------SERVER_INFO---------------------------------------------- */
data class ServerInfoDtoProperties(val authors: List<ServerAuthorDtoProperties>, val systemVersion: String)

typealias ServerInfoDto = SirenEntity<ServerInfoDtoProperties>
val ServerInfoDtoType = SirenEntity.getType<ServerInfoDtoProperties>()

data class ServerAuthorDtoProperties(val name: String, val email: String)

typealias ServerAuthorDto = SirenEntity<ServerAuthorDtoProperties>
val ServerAuthorDtoType = SirenEntity.getType<ServerAuthorDtoProperties>()





