package com.example.battleships.use_cases

import com.example.battleships.services.ApiException
import com.example.battleships.services.Either

internal fun <T> getValueOrThrow(either: Either<ApiException, T>): T {
    when (either) {
        is Either.Left -> throw either.value
        is Either.Right -> return either.value
    }
}