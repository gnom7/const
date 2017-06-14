package com.k.ktor.cons

import freemarker.cache.ClassTemplateLoader
import org.jetbrains.ktor.application.*
import org.jetbrains.ktor.features.StatusPages
import org.jetbrains.ktor.freemarker.FreeMarker
import org.jetbrains.ktor.freemarker.FreeMarkerContent
import org.jetbrains.ktor.http.HttpStatusCode
import org.jetbrains.ktor.locations.Locations
import org.jetbrains.ktor.locations.location
import org.jetbrains.ktor.request.host
import org.jetbrains.ktor.request.port
import org.jetbrains.ktor.response.respondRedirect
import org.jetbrains.ktor.response.respondText
import org.jetbrains.ktor.routing.Routing
import org.jetbrains.ktor.routing.get
import org.mindrot.jbcrypt.BCrypt
import java.util.*

@location("/home")
class Index()

@location("/register")
data class Register(val id: Long = -1, val username: String = "", val displayName: String = "", val email: String = "", val password: String = "", val error: String = "")

data class Session(val id: Long)

fun Application.main() {
//    install(DefaultHeaders)
//    install(CallLogging)
//    install(ConditionalHeaders)
//    install(PartialContentSupport)
    install(Locations)
//    install(Authentication)

    install(FreeMarker) {
        templateLoader = ClassTemplateLoader(this@main::class.java.classLoader, "templates")
    }

    install(StatusPages) {
        status(*HttpStatusCode.allStatusCodes.toTypedArray()) {
            call.respond(FreeMarkerContent("error.ftl", Collections.EMPTY_MAP, ""))
        }
    }

    intercept(ApplicationCallPipeline.Fallback) {
        //works after StatusPages feature from above
    }

    val hashFunction = { password: String -> BCrypt.hashpw(password, BCrypt.gensalt()) }

    install(Routing) {
        styles()
        webjars()
        index()
        register(hashFunction = hashFunction)

        get("/") {
            call.respondText("Hey")
        }
    }
}

suspend fun ApplicationCall.redirect(location: Any) {
    val host = request.host() ?: "localhost"
    val portSpec = request.port().let { if (it == 80) "" else ":$it" }
    val address = host + portSpec

    respondRedirect("http://$address${application.feature(Locations).href(location)}")
}
