
package com.example.battleships.dtos

import com.example.battleships.game.domain.state.Game
import com.example.battleships.game.domain.state.GameState
import com.example.battleships.utils.hypermedia.SirenEntity
import com.example.battleships.game.domain.board.Board
import com.example.battleships.game.domain.state.Configuration


data class HomeDtoProperties(val title: String)

typealias HomeDto = SirenEntity<HomeDtoProperties>
val HomeDtoType = SirenEntity.getType<HomeDtoProperties>()


data class RankingsDtoProperties(val username: String, val wins: Int, val gamesPlayed: Int)

typealias RankingsDto = SirenEntity<RankingsDtoProperties>
val RankingsDtoType = SirenEntity.getType<RankingsDtoProperties>()