package com.example.battleships.menu


interface UserService {
    fun createUser(username: String, password: String): Boolean
    fun login(username: String, password: String): Boolean
}

class FakeUserService : UserService {
    private val users = mutableMapOf("" to "")

    override fun createUser(username: String, password: String): Boolean {
        if (users.containsKey(username)) {
            return false
        }
        users[username] = password
        return true
    }

    override fun login(username: String, password: String): Boolean {
        if (!users.containsKey(username)) {
            return false
        }
        return users[username] == password
    }
}