package com.project.websocketchat

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.project.websocketchat.Constants.URL
import com.project.websocketchat.ui.theme.WebsocketChatTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket

class MainActivity : ComponentActivity() {
    private lateinit var webSocketListener: MyWebSocketListener
    private val okHttpClient = OkHttpClient()
    private var webSocket: WebSocket? = null
    private val viewModel: MainViewModel by viewModels()

    private val webSocketClient = KtorWebSocketClient()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        webSocketListener = MyWebSocketListener(viewModel)

        setContent {
            val coroutineScope = rememberCoroutineScope()
            var text by remember { mutableStateOf("") }

            WebsocketChatTheme {

                Column {
                    Button(onClick = {
                        //By OkHttpClient
//                        webSocket = okHttpClient.newWebSocket(createRequest(), webSocketListener)

                        //By Ktor
                        coroutineScope.launch(Dispatchers.IO) {
                            webSocketClient.connectToWebSocket()
                        }
                    }) {
                        Text(text = "Connect")
                    }

                    Button(onClick = {
                        //By OkHttpClient
//                        webSocket?.close(1000, "Canceled manually.")

                        //By Ktor
                        coroutineScope.launch(Dispatchers.IO) {
                            webSocketClient.disconnect()
                        }
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
                        //By OkHttpClient
//                        webSocket?.send(text)

                        //By Ktor
                        coroutineScope.launch(Dispatchers.IO) {
                            webSocketClient.sendMessage(text)
                        }
                    }) {
                        Text(text = "Send")
                    }
                }
            }

            DisposableEffect(Unit) {
                onDispose {
                    //By OkHttpClient
//                    webSocket?.close(1000, "Canceled manually.")

                    //By Ktor
                    coroutineScope.launch(Dispatchers.IO) {
                        webSocketClient.disconnect()
                        webSocketClient.closeClient()
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

    private fun createRequest() = Request.Builder().url(URL).build()

    override fun onDestroy() {
        super.onDestroy()
        okHttpClient.dispatcher.executorService.shutdown()
    }
}