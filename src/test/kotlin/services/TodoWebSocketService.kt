package services

import okhttp3.*
import wrappers.WsMessage
import utils.withAuth
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class TodoWebSocketService(
    private val wsUrl: String,
    val baseAuth: String,
    private val client: OkHttpClient = OkHttpClient()
) {
    fun connect(listener: WebSocketListener, credAuth: String?): WebSocket {
        val request = Request.Builder()
            .url(wsUrl)
            .withAuth(credAuth)
            .build()
        return client.newWebSocket(request, listener)
    }

    inline fun <reified T> listenOnceAfter(
        timeoutSeconds: Long = 10,
        credAuth: String? = baseAuth,
        crossinline action: () -> Unit
    ): WsMessage<T> {
        return listenManyAfter<T>(
            count = 1,
            timeoutSeconds = timeoutSeconds,
            credAuth = credAuth,
            action = action
        ).first()
    }

    inline fun <reified T> listenManyAfter(
        count: Int,
        timeoutSeconds: Long = 10,
        credAuth: String? = baseAuth,
        crossinline action: () -> Unit
    ): List<WsMessage<T>> {
        val ready = CompletableFuture<Unit>()
        val messages = Collections.synchronizedList(mutableListOf<WsMessage<T>>())
        val latch = CountDownLatch(count)

        val listener = object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                ready.complete(Unit)
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                messages += WsMessage.from<T>(text)
                if (latch.count == 1L) webSocket.close(1000, null)
                latch.countDown()
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                while (latch.count > 0) {
                    latch.countDown()
                }
            }
        }

        connect(listener, credAuth)
        ready.get(timeoutSeconds, TimeUnit.SECONDS)
        action()
        latch.await(timeoutSeconds, TimeUnit.SECONDS)

        return messages.toList()
    }
}
