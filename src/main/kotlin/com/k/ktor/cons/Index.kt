package com.k.ktor.cons

import org.jetbrains.ktor.application.call
import org.jetbrains.ktor.freemarker.FreeMarkerContent
import org.jetbrains.ktor.locations.get
import org.jetbrains.ktor.routing.Route
import java.util.*

fun Route.index() {
    get<Index> {
        call.respond(FreeMarkerContent("index.ftl", Collections.EMPTY_MAP, ""))
    }
}
