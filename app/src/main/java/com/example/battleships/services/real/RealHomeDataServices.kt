package com.example.battleships.services.real

import android.util.Log
import com.example.battleships.dtos.*
import com.example.battleships.home.Home
import com.example.battleships.info.ServerInfo
import com.example.battleships.info.serverInfo
import com.example.battleships.rankings.GameRanking
import com.example.battleships.rankings.rankings
import com.example.battleships.services.*
import com.example.battleships.utils.hypermedia.SirenAction
import com.example.battleships.utils.hypermedia.SirenLink
import com.example.battleships.utils.hypermedia.SirenMediaType
import com.example.battleships.utils.hypermedia.toApiURL
import com.example.battleships.utils.send
import com.google.gson.Gson
import okhttp3.OkHttpClient
import java.net.URL

class RealHomeDataServices(
    private val battleshipsHome: URL,
    private val httpClient: OkHttpClient,
    private val jsonEncoder: Gson
): HomeDataServices {
    private var userCreateAction: SirenAction? = null
    private var userLoginAction: SirenAction? = null
    private var userHomeLink: SirenLink? = null
    private var rankingsLink: SirenLink? = null
    private var serverInfoLink: SirenLink? = null

    private suspend fun getHome(): Home {
        val request = buildRequest(Get(battleshipsHome), null, Mode.AUTO)

        val homeDto = request.send(httpClient) { response ->
            handleResponse<HomeDto>(jsonEncoder, response, HomeDtoType.type, SirenMediaType)
        }
        getLinksAndActions(homeDto)
        if (userCreateAction == null || userLoginAction == null || userHomeLink == null
            || rankingsLink == null || serverInfoLink == null)
            throw UnresolvedLinkException()
                .also { Log.e("RealHomeDataServices", "Unresolved link", it) }

        return Home(homeDto)
    }

    override suspend fun getServerInfo(mode: Mode): ServerInfo {
        val serverInfoURL: URL = ensureServerInfoLink()
        val request = buildRequest(Get(serverInfoURL), null, mode)

        val serverInfoDto = request.send(httpClient) { response ->
            handleResponse<ServerInfoDto>(jsonEncoder, response, ServerInfoDtoType.type, SirenMediaType)
        }
        val serverInfoProperties = serverInfoDto.properties
        require(serverInfoProperties != null) { "ServerInfoDto properties should not have been null" }
        return serverInfo(serverInfoProperties)
    }

    override suspend fun getRankings(mode: Mode): GameRanking {
        val rankingsURL: URL = ensureRankingsLink()
        val request = buildRequest(Get(rankingsURL), null, mode)

        val rankingsDto = request.send(httpClient) { response ->
            handleResponse<RankingsDto>(jsonEncoder, response, RankingsDtoType.type, SirenMediaType)
        }
        val rankingsProperties = rankingsDto.properties
        require(rankingsProperties != null) { "ServerInfoDto properties should not have been null" }
        return rankings(rankingsProperties)
    }

    private fun getLinksAndActions(home : HomeDto){
        userCreateAction = home.actions?.find("create-user")
        userLoginAction = home.actions?.find("create-token")
        userHomeLink = home.links?.find("user-home")
        serverInfoLink = home.links?.find("server-info")
        rankingsLink = home.links?.find("user-stats")
    }

    private suspend fun ensureServerInfoLink(): URL {
        if (serverInfoLink == null) {
            getHome()
        }
        val action = serverInfoLink ?: throw UnresolvedLinkException()
        return action.href.toApiURL()
    }

    private suspend fun ensureRankingsLink(): URL {
        if (rankingsLink == null) {
            getHome()
        }
        val action = rankingsLink ?: throw UnresolvedLinkException()
            .also { Log.e("RealHomeDataServices", "Unresolved link", it) }
        return action.href.toApiURL()
    }

    suspend fun getCreateUserAction(): SirenAction {
        if (userCreateAction == null) {
            getHome()
            return userCreateAction ?: throw UnresolvedLinkException()
        }
        return userCreateAction ?: throw UnresolvedLinkException()
    }

    suspend fun getCreateTokenAction(): SirenAction {
        if (userLoginAction == null) {
            getHome()
            return userLoginAction ?: throw UnresolvedLinkException()
        }
        return userLoginAction ?: throw UnresolvedLinkException()
    }

    suspend fun getUserHomeLink(): SirenLink {
        if (userHomeLink == null) {
            getHome()
            return userHomeLink ?: throw UnresolvedLinkException()
        }
        return userHomeLink ?: throw UnresolvedLinkException()
    }
}