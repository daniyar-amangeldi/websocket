package com.example.websocket

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import java.util.concurrent.TimeUnit

interface IWebSocketManager {
    suspend fun startToListen(requestUrl: String): Flow<SocketState>
    suspend fun sendMessage(message: String): Boolean
    fun close(code: Int, reason: String): Boolean
}

class WebSocketManager(
    private val baseUrl: String,
    private val connectTimeout: Long = DEFAULT_TIMEOUT,
    private val readTimeout: Long = DEFAULT_TIMEOUT,
    private val writeTimeout: Long = DEFAULT_TIMEOUT,
    private val pingInterval: Long = DEFAULT_PING_INTERVAL
) : IWebSocketManager {

    companion object {
        private const val DEFAULT_TIMEOUT = 60L
        private const val DEFAULT_PING_INTERVAL = 25L
    }

    private var webSocket: WebSocket? = null

    private val client by lazy {
        OkHttpClient.Builder().apply {
            connectTimeout(connectTimeout, TimeUnit.SECONDS)
            readTimeout(readTimeout, TimeUnit.SECONDS)
            writeTimeout(writeTimeout, TimeUnit.SECONDS)
            pingInterval(pingInterval, TimeUnit.SECONDS)
            retryOnConnectionFailure(true)
        }.build()
    }

    override suspend fun startToListen(requestUrl: String): Flow<SocketState> {
        val request = Request.Builder()
            .url(baseUrl + requestUrl)
            .addHeader("Sec-Websocket-Key", "nu/EdwVyvcknrM8aHxdHGA==")
            .addHeader("Sec-Websocket-Version", "13")
            .build()

        val flow = MutableStateFlow<SocketState>(SocketState.Opened)
        webSocket = client.newWebSocket(request, SocketListener(flow))

        return flow
    }

    override suspend fun sendMessage(message: String): Boolean {
        return webSocket?.send(message) ?: false
    }

    override fun close(code: Int, reason: String) = webSocket?.close(code, reason) ?: false
}