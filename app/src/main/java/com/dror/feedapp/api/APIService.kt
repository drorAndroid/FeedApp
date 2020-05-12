package com.dror.feedapp.api

import com.dror.feedapp.di.DaggerApplicationComponent
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import okhttp3.*
import okio.ByteString

class APIService(private val client: OkHttpClient) {
    val remoteURL = "ws://superdo-groceries.herokuapp.com/receive"
    private val onMessageSubject = PublishSubject.create<String>()
    val onMessageEvent : Observable<String> = onMessageSubject
    private val onFailureSubject = PublishSubject.create<String>()
    val onFailureEvent : Observable<String> = onFailureSubject
    private var ws: WebSocket? = null

   init {
       DaggerApplicationComponent.create().inject(this)
   }

    fun start() {
        val request: Request = Request.Builder().url(remoteURL).build()
        val listener = CustomWebSocketListener()
        ws = client.newWebSocket(request, listener)
        client.dispatcher().executorService().shutdown()
    }

    fun stop() {
        ws?.close(1000, "")
    }

    inner class CustomWebSocketListener : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response?) {
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            onMessageSubject.onNext(text)
        }

        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        }

        override fun onClosing(
            webSocket: WebSocket,
            code: Int,
            reason: String
        ) {
            webSocket.close(NORMAL_CLOSURE_STATUS, null)
        }

        override fun onFailure(
            webSocket: WebSocket?,
            t: Throwable,
            response: Response?
        ) {
            t.message?.let {
                onFailureSubject.onNext(it)
            }
        }
    }

    companion object {
        private const val NORMAL_CLOSURE_STATUS = 1000
    }
}