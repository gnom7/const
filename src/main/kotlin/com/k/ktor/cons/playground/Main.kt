package com.k.ktor.cons.playground

import kotlinx.coroutines.experimental.*
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.system.measureTimeMillis

/**
 * @since 13.06.17
 */

//fun main(args: Array<String>) = runBlocking<Unit> {
//    val dispatcher = Executors.newFixedThreadPool(10).asCoroutineDispatcher()
//    val time = measureTimeMillis {
//        val one = async(CommonPool) { doOne() }
//        val two = async(CommonPool) { doTwo() }
//        println("The answer is ${one.await() + two.await()}")
//    }
//    println("Completed in $time ms")
//}

//fun main(args: Array<String>) {
//    val time = measureTimeMillis {
//        val one = asyncDoOne()
//        val two = asyncDoTwo()
//        runBlocking {
//            println("The answer is ${one.await() + two.await()}")
//        }
//    }
//    println("Completed in $time ms")
//}

fun main(args: Array<String>) = runBlocking<Unit> {
    val jobs = arrayListOf<Job>()
    jobs += launch(Unconfined) {
        println("Unconfined: ${Thread.currentThread().name}")
        delay(1L, TimeUnit.SECONDS)
        println("Unconfined after delay: ${Thread.currentThread().name}")
    }
    jobs += launch(context) {
        println("context: ${Thread.currentThread().name}")
        delay(1L, TimeUnit.SECONDS)
        println("context after delay: ${Thread.currentThread().name}")
    }
    jobs += launch(CommonPool) {
        println("CommonPool: ${Thread.currentThread().name}")
    }
    jobs += launch(newSingleThreadContext("Own")) {
        println("Own: ${Thread.currentThread().name}")
    }
    jobs.forEach {
        it.join()
    }
}

suspend fun doOne(): Int {
    delay(1L, TimeUnit.SECONDS)
    return 13
}

suspend fun doTwo(): Int {
    delay(1L, TimeUnit.SECONDS)
    return 29
}

fun asyncDoOne() = async(CommonPool) {
    doOne()
}

fun asyncDoTwo() = async(CommonPool) {
    doTwo()
}
