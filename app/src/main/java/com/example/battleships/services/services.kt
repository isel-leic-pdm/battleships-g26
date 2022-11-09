package com.example.battleships.services

/**
 * Used to identify how implementations SHOULD behave:
 * - [FORCE_REMOTE] is used to indicate that the operation MUST try to access
 * the remote data source
 * - [FORCE_LOCAL] is usd to indicate that the operation SHOULD only use the
 * the local version of the data, if available
 * - [AUTO] states that the selection of which data to use is left to the
 * implementation.
 */
enum class Mode { FORCE_REMOTE, FORCE_LOCAL, AUTO }