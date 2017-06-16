package com.k.ktor.cons

import com.k.ktor.cons.dao.UserRepository
import com.typesafe.config.Config
import com.typesafe.config.ConfigFactory
import freemarker.cache.ClassTemplateLoader
import org.jetbrains.exposed.sql.Database
import org.jetbrains.ktor.application.Application
import org.jetbrains.ktor.application.ApplicationCall
import org.jetbrains.ktor.application.ApplicationCallPipeline
import org.jetbrains.ktor.application.call
import org.jetbrains.ktor.application.feature
import org.jetbrains.ktor.application.install
import org.jetbrains.ktor.auth.Authentication
import org.jetbrains.ktor.auth.UserIdPrincipal
import org.jetbrains.ktor.auth.formAuthentication
import org.jetbrains.ktor.features.ConditionalHeaders
import org.jetbrains.ktor.features.DefaultHeaders
import org.jetbrains.ktor.features.PartialContentSupport
import org.jetbrains.ktor.features.StatusPages
import org.jetbrains.ktor.freemarker.FreeMarker
import org.jetbrains.ktor.freemarker.FreeMarkerContent
import org.jetbrains.ktor.locations.Locations
import org.jetbrains.ktor.locations.location
import org.jetbrains.ktor.logging.CallLogging
import org.jetbrains.ktor.request.host
import org.jetbrains.ktor.request.port
import org.jetbrains.ktor.response.respondRedirect
import org.jetbrains.ktor.response.respondText
import org.jetbrains.ktor.routing.Routing
import org.jetbrains.ktor.routing.get
import org.jetbrains.ktor.sessions.SessionCookieTransformerMessageAuthentication
import org.jetbrains.ktor.sessions.SessionCookiesSettings
import org.jetbrains.ktor.sessions.withCookieByValue
import org.jetbrains.ktor.sessions.withSessions
import org.jetbrains.ktor.util.hex
import org.mindrot.jbcrypt.BCrypt
import java.io.File

@location("/home")
class Index()

@location("/register")
data class Register(val username: String = "", val displayName: String = "", val email: String = "", val password: String = "", val error: String = "")

@location("/login")
data class Login(val username: String = "", val password: String = "", val error: String = "")

data class Session(val id: Int)

val config: Config = ConfigFactory.load()

fun Application.main() {
    val dbFilePath = File("target/db").absolutePath//.let { File("$it.mv.db").delete(); it }
    Database.connect("jdbc:h2:file:$dbFilePath", driver = "org.h2.Driver")

    install(DefaultHeaders)
    install(CallLogging)
    install(ConditionalHeaders)
    install(PartialContentSupport)
    install(Locations)

    val userRepo = UserRepository()

    withSessions<Session> {
        withCookieByValue {
            val hashKey = hex(config.getString("cons.cookie.secret.key"))
            settings = SessionCookiesSettings(transformers = listOf(SessionCookieTransformerMessageAuthentication(hashKey)))
        }
    }

    install(FreeMarker) {
        templateLoader = ClassTemplateLoader(this@main::class.java.classLoader, "templates")
    }

    install(StatusPages) {
        exception<Throwable> { cause ->
            call.respond(FreeMarkerContent("error.ftl", mapOf("error" to cause), ""))
        }
    }

    intercept(ApplicationCallPipeline.Fallback) {
        //works after StatusPages feature from above
    }

    install(Routing) {
        styles()
        webjars()
        index()
        register(userRepo, { s: String -> hash(s) })
        login(userRepo, { row: String, hash: String -> checkHash(row, hash) })

        get("/") {
            call.respondText("Hey")
        }
    }
}

fun hash(password: String): String {
    return BCrypt.hashpw(password, BCrypt.gensalt())
}

fun checkHash(row: String, hash: String): Boolean {
    return BCrypt.checkpw(row, hash)
}

suspend fun ApplicationCall.redirect(location: Any) {
    val host = request.host() ?: "localhost"
    val portSpec = request.port().let { if (it == 80) "" else ":$it" }
    val address = host + portSpec

    respondRedirect("http://$address${application.feature(Locations).href(location)}")
}

private val userIdPattern = "[a-zA-Z0-9_\\.]+".toRegex()
internal fun userNameValid(userId: String) = userId.matches(userIdPattern)
