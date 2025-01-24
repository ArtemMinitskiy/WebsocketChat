package com.project.websocketchat

import android.util.Log
import com.project.websocketchat.Constants.URL
import io.ktor.client.HttpClient
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.webSocket
import io.ktor.websocket.CloseReason
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import io.ktor.websocket.readText

class KtorWebSocketClient {
    private val client = HttpClient {
        install(WebSockets)
    }

    private var session: DefaultClientWebSocketSession? = null

    suspend fun connectToWebSocket() {
        try {
            client.webSocket(URL) {
                session = this
                Log.d("mLogTest", "Connected to $URL")

                for (message in incoming) {
                    when (message) {
                        is Frame.Text -> Log.d(
                            "mLogTest",
                            "Message received: ${message.readText()}"
                        )

                        is Frame.Binary -> Log.d("mLogTest", "Binary message received")
                        else -> Log.d("mLogTest", "Unknown frame received")
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("mLogTest", "Error: $e")
        }
    }

    suspend fun sendMessage(message: String) {
        try {
            session?.send(Frame.Text(message))
            Log.d("mLogTest", "Message sent: $message")
        } catch (e: Exception) {
            Log.e("mLogTest", "Error sending message: $e")
        }
    }

    suspend fun disconnect() {
        try {
            session?.close(CloseReason(CloseReason.Codes.NORMAL, "Session closed by user"))
            session = null
            Log.d("mLogTest", "Disconnected from WebSocket")
        } catch (e: Exception) {
            Log.e("mLogTest", "Error during disconnect: $e")
        }
    }

    fun closeClient() {
        client.close()
        Log.d("mLogTest", "HttpClient closed")
    }
}

