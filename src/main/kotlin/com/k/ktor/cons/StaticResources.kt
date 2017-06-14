package com.k.ktor.cons

import org.jetbrains.ktor.application.call
import org.jetbrains.ktor.content.resolveResource
import org.jetbrains.ktor.request.uri
import org.jetbrains.ktor.routing.Route
import org.jetbrains.ktor.routing.get

fun Route.styles() {
    get("/styles/main.css") {
        call.respond(call.resolveResource("main.css")!!)
    }
}

fun Route.webjars() {
    get("/webjars/{path...}") {
        call.respond(call.resolveResource("/META-INF/resources${call.request.uri}")!!)
    }
}
