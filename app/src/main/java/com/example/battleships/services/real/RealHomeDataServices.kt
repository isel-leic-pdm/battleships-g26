package com.example.battleships.services.real

import com.example.battleships.dtos.*
import com.example.battleships.home.Home
import com.example.battleships.info.ServerInfo
import com.example.battleships.info.serverInfo
import com.example.battleships.rankings.GameRanking
import com.example.battleships.rankings.rankings
import com.example.battleships.services.*
import com.example.battleships.services.buildRequest
import com.example.battleships.services.handleResponse
import com.example.battleships.utils.hypermedia.SirenAction
import com.example.battleships.utils.hypermedia.SirenLink
import com.example.battleships.utils.send
import com.google.gson.Gson
import okhttp3.OkHttpClient
import java.net.URL

class RealHomeDataServices(
    private val battleshipsHome: URL,
    private val httpClient: OkHttpClient,
    private val jsonEncoder: Gson
): HomeDataServices {
    var rankingsLink: SirenLink? = null
    var serverInfoLink: SirenLink? = null
    var userCreateAction: SirenAction? = null
    var userLoginAction: SirenAction? = null

    private suspend fun getHome(): Home {
        val request = buildRequest(Get(battleshipsHome), Mode.FORCE_REMOTE)

        val homeDto = request.send(httpClient) { response ->
            handleResponse<HomeDto>(jsonEncoder, response, HomeDtoType.type)
        }

        rankingsLink = getRankingsLink(homeDto)
        userCreateAction = getCreateUserAction(homeDto)
        userLoginAction = getUserLoginAction(homeDto)
        if (rankingsLink == null || userCreateAction == null || userLoginAction == null)
            throw UnresolvedLinkException()

        return Home(homeDto)
    }

    internal suspend fun getServerInfo(mode: Mode): ServerInfo {
        val serverInfoURL: URL = ensureServerInfoLink()
        val request = buildRequest(Get(serverInfoURL), mode)

        val serverInfoDto = request.send(httpClient) { response ->
            handleResponse<HomeDto>(jsonEncoder, response, HomeDtoType.type)
        }
        val serverInfoProperties = serverInfoDto.properties
        require(serverInfoProperties != null) { "ServerInfoDto properties should not have been null" }
        return serverInfo(serverInfoProperties)
    }

    internal suspend fun getRankings(mode: Mode): GameRanking {
        val rankingsURL: URL = ensureRankingsLink()
        val request = buildRequest(Get(rankingsURL), mode)

        val rankingsDto = request.send(httpClient) { response ->
            handleResponse<RankingsDto>(jsonEncoder, response, RankingsDtoType.type)
        }
        val rankingsProperties = rankingsDto.properties
        require(rankingsProperties != null) { "ServerInfoDto properties should not have been null" }
        return rankings(rankingsProperties)
    }

    /**
     * Navigates [home] in search of the link for the APIs resource
     * bearing the week's quotes.
     * @return the link found in the DTO, or null
     */
    fun getRankingsLink(home: HomeDto) =
        home.links?.find { it.rel.contains("user-stats") }

    fun getCreateUserAction(home: HomeDto) =
        home.actions?.find { it.name == "create-user" }

    fun getUserLoginAction(home: HomeDto) =
        home.actions?.find { it.name == "login" }

    suspend fun ensureServerInfoLink(requestParams: RequestParams): URL {
        if (serverInfoLink == null) {
            getHome(requestParams)
        }
        val action = serverInfoLink ?: throw UnresolvedLinkException()
        return action.href.toURL()
    }

    suspend fun ensureRankingsLink(): URL {
        if (rankingsLink == null) {
            getHome()
        }
        val link = rankingsLink ?: throw UnresolvedLinkException()
        return link.href.toURL()
    }

    suspend fun ensureUserCreateAction(): SirenAction {
        if (userCreateAction == null) {
            getHome()
        }
        val action = userCreateAction ?: throw UnresolvedLinkException()
        return action
    }

    suspend fun ensureUserLoginAction(): SirenAction {
        if (userLoginAction == null) {
            getHome()
        }
        val action = userLoginAction ?: throw UnresolvedLinkException()
        return action
    }
}