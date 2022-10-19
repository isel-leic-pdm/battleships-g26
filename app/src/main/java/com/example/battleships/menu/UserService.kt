package com.example.battleships.menu


interface UserService {
    fun createUser(username: String, password: String): Boolean
    fun login(username: String, password: String): String?
}

data class Credentials(val username: String, val password: String)

class FakeUserService : UserService {
    private val users = mutableMapOf(
        1 to Credentials("", ""),
        2 to Credentials("user2", "password2"),
        3 to Credentials("user3", "password3"),
        4 to Credentials("user4", "password4"),
        5 to Credentials("user5", "password5"),
    )
    private val tokens = mutableMapOf(
        1 to "token1",
        2 to "token2",
        3 to "token3",
        4 to "token4",
        5 to "token5",
    )

    override fun createUser(username: String, password: String): Boolean {
        val id = users.size + 1
        users[id] = Credentials(username, password)
        tokens[id] = "token$id"
        return true
    }

    /**
     * Checks if username and password are valid, and, if so, returns associated token.
     */
    override fun login(username: String, password: String): String? {
        val users = users
            .filter { it.value.username == username && it.value.password == password }
            .toList()
        return if (users.isEmpty()) {
            null
        } else {
            tokens[users.first().first]
        }
    }
}