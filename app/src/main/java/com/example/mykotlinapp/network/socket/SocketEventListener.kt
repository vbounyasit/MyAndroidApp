package com.example.mykotlinapp.network.socket

/**
 * SAM interface to process data received from server socket
 *
 * @param EventData The type of data received from an event
 */
fun interface SocketEventListener<EventData> {
    fun processData(data: EventData)
}