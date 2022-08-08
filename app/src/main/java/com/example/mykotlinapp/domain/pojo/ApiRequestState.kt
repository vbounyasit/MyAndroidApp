package com.example.mykotlinapp.domain.pojo

/**
 * Api request state displayed on various UI screens
 *
 * @constructor Create an Api request state
 */
enum class ApiRequestState {
    FAILED, FAILED_UNAUTHORIZED, LOADING, SUCCESS, FINISHED
}