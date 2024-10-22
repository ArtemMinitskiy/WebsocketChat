package com.project.websocketchat

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.project.websocketchat.ui.theme.WebsocketChatTheme
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket

class MainActivity : ComponentActivity() {
    private lateinit var webSocketListener: MyWebSocketListener
    private val okHttpClient = OkHttpClient()
    private var webSocket: WebSocket? = null
    private val CLUSTER_ID = "CLUSTER_ID"
    private val API_KEY = "API_KEY"
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        webSocketListener = MyWebSocketListener(viewModel)

        setContent {
            var text by remember { mutableStateOf("") }

            WebsocketChatTheme {

                Column {
                    Button(onClick = {
                        webSocket = okHttpClient.newWebSocket(createRequest(), webSocketListener)

                    }) {
                        Text(text = "Connect")
                    }

                    Button(onClick = {
                        webSocket?.close(1000, "Canceled manually.")
                    }) {
                        Text(text = "Disconnect")
                    }

                    TextField(
                        value = text,
                        onValueChange = { text = it },
                        label = { Text(text = "Enter your text") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(onClick = {
                        webSocket?.send(text)
                    }) {
                        Text(text = "Send")
                    }
                }
            }
        }

        viewModel.socketStatus.observeForever {
            Log.i("mLogTest", "socketStatus $it")
        }
        viewModel.messages.observeForever {
            Log.i("mLogTest", "messages $it")
        }

    }

    private fun createRequest(): Request {
        val websocketURL = "wss://${CLUSTER_ID}.piesocket.com/v3/1?api_key=${API_KEY}"

        return Request.Builder()
            .url(websocketURL)
            .build()
    }

    override fun onDestroy() {
        super.onDestroy()
        okHttpClient.dispatcher.executorService.shutdown()
    }
}