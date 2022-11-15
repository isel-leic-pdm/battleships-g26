package com.example.battleships.info

import com.example.battleships.dtos.ServerInfoDtoProperties

data class ServerInfo(
    val authors: List<ServerAuthor>,
    val systemVersion: String
)

fun serverInfo(serverInfoDtoProperties: ServerInfoDtoProperties) =
    ServerInfo(
        serverInfoDtoProperties.authors.map { ServerAuthor(it.name, it.email) },
        serverInfoDtoProperties.systemVersion
    )

data class ServerAuthor(
    val name: String,
    val email: String
)