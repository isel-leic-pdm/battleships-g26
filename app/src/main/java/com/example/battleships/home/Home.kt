package com.example.battleships.home

import com.example.battleships.dtos.HomeDto

data class Home(val title: String)

fun Home(dto: HomeDto): Home {
    val home = dto.properties
    require(home != null) { "HomeDto properties cannot be null" }
    return Home(home.title)
}