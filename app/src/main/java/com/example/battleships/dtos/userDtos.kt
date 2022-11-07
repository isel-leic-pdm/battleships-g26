
package com.example.battleships.dtos

import com.example.battleships.utils.hypermedia.SirenEntity

data class CreateUserDtoProperties(val userId: Int)

typealias CreateUserDto = SirenEntity<CreateUserDtoProperties>
val CreateUserDtoType = SirenEntity.getType<CreateUserDtoProperties>()


data class UserLoginDtoProperties(val token: String)

typealias UserLoginDto = SirenEntity<UserLoginDtoProperties>
val UserLoginDtoType = SirenEntity.getType<UserLoginDtoProperties>()


fun CreateUserDto.toUserId(): Int {
    val properties = this.properties
    require(properties != null) { "Properties are null" }
    return properties.userId
}

fun UserLoginDto.toToken(): String {
    val properties = this.properties
    require(properties != null) { "UserLoginDto properties are null" }
    return properties.token
}