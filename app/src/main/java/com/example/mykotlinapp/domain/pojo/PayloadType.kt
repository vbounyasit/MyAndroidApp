package com.example.mykotlinapp.domain.pojo

/**
 * Payload type representing the various keys used to pass data as payloads to recycler view items to update them
 *
 * @constructor Create a Payload type
 */
enum class PayloadType {
    PAYLOAD_VOTE_STATE,
    PAYLOAD_EDIT_STATE,
    PAYLOAD_NOTIFICATION_RULE,
    PAYLOAD_CONTACT_REQUEST_TYPE,
}