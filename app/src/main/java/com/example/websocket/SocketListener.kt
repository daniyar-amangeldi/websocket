package com.example.websocket

import kotlinx.coroutines.flow.MutableStateFlow
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

class SocketListener(
    private val publisher: MutableStateFlow<SocketState>
) : WebSocketListener() {

    override fun onOpen(webSocket: WebSocket, response: Response) {
        publisher.value = SocketState.Opened
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        publisher.value = SocketState.Message(text)
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        publisher.value = SocketState.Closed
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        publisher.value = SocketState.Fail(t as Exception)
    }

}