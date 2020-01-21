package com.example.rssreader.search


import com.example.rssreader.search.ResultsCounter.actor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.newSingleThreadContext

enum class Action {
    INCREASE,
    RESET
}

object ResultsCounter {
    private val context = newSingleThreadContext("counter")
    private var counter = 0
    private val notifications = Channel<Int>(Channel.CONFLATED)


    private val actor = CoroutineScope(context).actor<Action>(context) {
        for (msg in channel) {
            when (msg) {
                Action.INCREASE -> counter++
                Action.RESET -> counter = 0
            }
            notifications.send(counter)
        }
    }

    suspend fun increment() = actor.send(Action.INCREASE)
    suspend fun reset() = actor.send(Action.RESET)

    fun getNotificationChannel(): ReceiveChannel<Int> = notifications


}

