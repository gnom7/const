package com.k.ktor.cons.playground

import kotlinx.coroutines.experimental.*
import java.util.concurrent.TimeUnit

/**
 * @since 13.06.17
 */

fun main(args: Array<String>) = runBlocking<Unit> {
    val a = async(context) {
        log("I'm computing a piece of the answer")
        6
    }
    val b = async(context) {
        log("I'm computing another piece of the answer")
        7
    }
    log("The answer is ${a.await() * b.await()}")

    println("===========")

    val ctx1 = newSingleThreadContext("Ctx1")
    val ctx2 = newSingleThreadContext("Ctx2")
    runBlocking(ctx1) {
        log("Started in ctx1")
        run(ctx2) {
            log("Working in ctx2")
            delay(1L, TimeUnit.SECONDS)
        }
        log("Back to ctx1")
    }

    println("My job is ${context[Job]}")
}

fun log(msg: String) = println("[${Thread.currentThread().name}] $msg")
