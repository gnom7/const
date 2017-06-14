package com.k.ktor.cons.playground

import kotlinx.coroutines.experimental.*
import org.jetbrains.ktor.util.NoopContinuation.context

/**
 * @since 13.06.17
 */

fun main(args: Array<String>) = runBlocking<Unit> {
    // start a coroutine to process some kind of incoming request
    val request = launch(CommonPool) {
        // it spawns two other jobs, one with its separate context
        val job1 = launch(CommonPool) {
            log("job1: I have my own context and execute independently!")
            delay(1000)
            log("job1: I am not affected by cancellation of the request")
        }
        // and the other inherits the parent context
        val job2 = launch(context) {
            log("job2: I am a child of the request coroutine")
            delay(1000)
            log("job2: I will not execute this line if my parent request is cancelled")
        }
        // request completes when both its sub-jobs complete:
        job1.join()
        job2.join()
        log("parent: Join end.")
    }
    delay(500)
    request.cancel() // cancel processing of the request
    delay(1000) // delay a second to see what happens
    log("main: Who has survived request cancellation?")
}
