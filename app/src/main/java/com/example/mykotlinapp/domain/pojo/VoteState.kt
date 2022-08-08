package com.example.mykotlinapp.domain.pojo

/**
 * Vote state for posts or comments
 *
 * @property value The vote value to send to the server
 * @constructor Create a Vote state
 */
enum class VoteState(val value: Int) {
    UP_VOTED(1), DOWN_VOTED(-1), NONE(0);
}