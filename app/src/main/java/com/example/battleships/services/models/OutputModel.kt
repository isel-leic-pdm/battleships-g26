package com.example.battleships.services.models

import com.google.gson.Gson

interface OutputModel {
    fun toJson(encoder : Gson): String = encoder.toJson(this)
}