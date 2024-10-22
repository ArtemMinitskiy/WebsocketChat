package com.project.websocketchat

import android.util.Log
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

class MyWebSocketListener(
    private val viewModel: MainViewModel
) : WebSocketListener() {

    override fun onOpen(webSocket: WebSocket, response: Response) {
        super.onOpen(webSocket, response)
        viewModel.setStatus(true)
        webSocket.send("Android Device Connected")
        Log.d("mLogTest", "onOpen:")
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        super.onMessage(webSocket, text)
        viewModel.addMessage(Pair(false, text))
        Log.d("mLogTest", "onMessage: $text")
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosing(webSocket, code, reason)
        Log.d("mLogTest", "onClosing: $code $reason")
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosed(webSocket, code, reason)
        viewModel.setStatus(false)
        Log.d("mLogTest", "onClosed: $code $reason")
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        Log.d("mLogTest", "onFailure: ${t.message} $response")
        super.onFailure(webSocket, t, response)
    }
}