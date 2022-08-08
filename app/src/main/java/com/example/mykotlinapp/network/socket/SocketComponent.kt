package com.example.mykotlinapp.network.socket

import android.util.Log
import com.example.mykotlinapp.model.dao.SharedPreferenceDao
import com.example.mykotlinapp.network.ApiServiceProvider
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import io.socket.client.IO
import io.socket.client.Socket
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

/**
 * Helper class for initializing, connecting and disconnecting Socket
 * alongside other helper functions
 * @Injected in an activity viewModel
 */
class SocketComponent(
    private val sharedPreferenceDao: SharedPreferenceDao,
    val dispatcher: CoroutineDispatcher,
    val moshi: Moshi,
) {

    private var _socket: Result<Socket>? = null

    val socket: Result<Socket>?
        get() = _socket

    /**
     * Initializes the socket object with user authentication header
     */
    suspend fun initialize() {
        _socket = try {
            sharedPreferenceDao.getUserAuthToken()?.let {
                val headers = IO.Options.builder()
                    .setExtraHeaders(mapOf("authorization" to listOf("bearer $it")))
                    .build()
                Result.success(IO.socket(ApiServiceProvider.BASE_URL, headers))
            }
        } catch (exception: Exception) {
            Log.e(TAG, "Failed to initialize Socket object")
            Result.failure(exception)
        }
    }

    /**
     * Connects the socket
     */
    suspend fun connect() = withContext(dispatcher) {
        _socket?.getOrNull()?.connect()
    }

    /**
     * Disconnects the socket
     */
    suspend fun disconnect() = withContext(dispatcher) {
        _socket?.getOrNull()?.disconnect()
        socket?.getOrNull()?.off()
    }

    /**
     * Adds an event listener to the socket
     *
     * @param T The type of the data received from the server socket
     * @param eventName The name of the socket event received
     * @param eventListener The socket event listener containing the action to execute
     */
    inline fun <reified T> addEvent(eventName: String, eventListener: SocketEventListener<T>) {
        socket?.getOrNull()?.on(eventName) {
            if (it.isNotEmpty()) {
                val jsonAdapter: JsonAdapter<T> = moshi.adapter(T::class.java)
                val eventData: T? = jsonAdapter.fromJson(it.first().toString())
                eventData?.let { data -> eventListener.processData(data) }
            }
        }
    }

    companion object {
        const val TAG = "SocketComponent"
    }
}