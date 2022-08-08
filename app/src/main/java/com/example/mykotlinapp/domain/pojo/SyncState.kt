package com.example.mykotlinapp.domain.pojo

/**
 * Sync state for a given database row used for synchronizing data to the API
 *
 * @constructor Create a Sync state
 */
enum class SyncState {
    PENDING_REMOVAL, PENDING_UPDATE, UP_TO_DATE
}