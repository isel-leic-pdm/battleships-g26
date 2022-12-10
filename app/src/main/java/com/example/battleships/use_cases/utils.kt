package com.example.battleships.use_cases

import com.example.battleships.services.Either

internal fun <T> getValueOrThrow(either: Either<Unit, T>, throwable: Throwable): T {
    when (either) {
        is Either.Right -> return either.value
        is Either.Left -> throw throwable
    }
}