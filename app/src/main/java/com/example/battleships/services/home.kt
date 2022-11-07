package com.example.battleships.services

import com.example.battleships.dtos.*
import com.example.battleships.home.Home
import com.example.battleships.info.ServerInfo
import com.example.battleships.info.serverInfo
import com.example.battleships.rankings.GameRanking
import com.example.battleships.rankings.rankings
import com.example.battleships.utils.hypermedia.SirenLink
import com.example.battleships.utils.send
import java.net.URL

internal suspend fun getHome(battleshipsHomeLink: URL, requestParams: RequestParams): Home {
    val request = buildRequest(Get(battleshipsHomeLink), requestParams.mode)

    val homeDto = request.send(requestParams.client) { response ->
        handleResponse<HomeDto>(requestParams.jsonEncoder, response, HomeDtoType.type)
    }

    rankingsLink = getRankingsLink(homeDto)
    userCreateAction = getCreateUserAction(homeDto)
    userLoginAction = getUserLoginAction(homeDto)
    if (rankingsLink == null || userCreateAction == null || userLoginAction == null)
        throw UnresolvedLinkException()

    return Home(homeDto)
}

internal suspend fun getServerInfoInternal(requestParams: RequestParams): ServerInfo {
    val serverInfoURL: URL = ensureServerInfoLink()
    val request = buildRequest(Get(serverInfoURL), requestParams.mode)

    val serverInfoDto = request.send(requestParams.client) { response ->
        handleResponse<HomeDto>(requestParams.jsonEncoder, response, HomeDtoType.type)
    }
    val serverInfoProperties = serverInfoDto.properties
    require(serverInfoProperties != null) { "ServerInfoDto properties should not have been null" }
    return serverInfo(serverInfoProperties)
}

internal suspend fun getRankingsInternal(requestParams: RequestParams): GameRanking {
    val rankingsURL: URL = ensureRankingsLink()
    val request = buildRequest(Get(rankingsURL), requestParams.mode)

    val rankingsDto = request.send(requestParams.client) { response ->
        handleResponse<RankingsDto>(requestParams.jsonEncoder, response, RankingsDtoType.type)
    }
    val rankingsProperties = rankingsDto.properties
    require(rankingsProperties != null) { "ServerInfoDto properties should not have been null" }
    return rankings(rankingsProperties)
}
/**
 * Links for the APIs resource bearing the game ranking and server info, or null if not
 * yet discovered.
 */
internal var rankingsLink: SirenLink? = null

internal var serverInfoLink: SirenLink? = null

/**
 * Navigates [home] in search of the link for the APIs resource
 * bearing the week's quotes.
 * @return the link found in the DTO, or null
 */
internal fun getRankingsLink(home: HomeDto) =
    home.links?.find { it.rel.contains("user-stats") }

internal fun getCreateUserAction(home: HomeDto) =
    home.actions?.find { it.name == "create-user" }

internal fun getUserLoginAction(home: HomeDto) =
    home.actions?.find { it.name == "login" }

/**
 * Makes sure we have the required link, if necessary, by navigating again
 * through the APIs responses, starting at the home resource (the home page, in this case).
 *
 * @return the [URL] instance representing the link for the week's quotes
 * @throws [UnresolvedLinkException] if the link could not be found
 */
internal suspend fun ensureRankingsLink(): URL {
    if (rankingsLink == null) {
        getHome()
    }
    val link = rankingsLink ?: throw UnresolvedLinkException()
    return link.href.toURL()
}

/**
 * @see [ensureRankingsLink]
 */
internal suspend fun ensureServerInfoLink(): URL {
    if (serverInfoLink == null) {
        getHome()
    }
    val link = serverInfoLink ?: throw UnresolvedLinkException()
    return link.href.toURL()
}
