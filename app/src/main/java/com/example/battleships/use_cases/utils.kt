package com.example.battleships.use_cases

import com.example.battleships.services.ApiException
import com.example.battleships.services.Either

internal fun <T> getOrThrowValue(either: Either<ApiException, T>): T {
    when (either) {
        is Either.Right -> return either.value
        is Either.Left -> throw either.value
    }
}