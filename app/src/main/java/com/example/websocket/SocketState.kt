package com.example.websocket

sealed class SocketState {
    data object Opened : SocketState()
    data class Message(val data: String) : SocketState()
    data class Fail(val exception: Exception) : SocketState()
    data object Closed : SocketState()
}