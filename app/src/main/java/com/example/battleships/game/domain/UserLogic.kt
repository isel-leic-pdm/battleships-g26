package com.example.battleships.game.domain

import android.os.Build
import androidx.annotation.RequiresApi
import java.security.SecureRandom
import java.util.*

@RequiresApi(Build.VERSION_CODES.O)
class UserLogic {

    fun generateToken(): String =
        ByteArray(TOKEN_BYTE_SIZE).let { byteArray ->
            SecureRandom.getInstanceStrong().nextBytes(byteArray)
            Base64.getUrlEncoder().encodeToString(byteArray)
        }

    fun canBeToken(token: String): Boolean = try {
        Base64.getUrlDecoder()
            .decode(token).size == TOKEN_BYTE_SIZE
    } catch (ex: IllegalArgumentException) {
        false
    }

    fun isSafePassword(password: String) = password.length > 4 && password.any{ it.isUpperCase() }

    companion object {
        private const val TOKEN_BYTE_SIZE = 256 / 8
    }
}