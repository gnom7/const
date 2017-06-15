package com.k.ktor.cons

import com.k.ktor.cons.dao.UserRepository
import org.jetbrains.ktor.application.call
import org.jetbrains.ktor.freemarker.FreeMarkerContent
import org.jetbrains.ktor.locations.get
import org.jetbrains.ktor.locations.post
import org.jetbrains.ktor.routing.Route
import org.jetbrains.ktor.sessions.session
import org.jetbrains.ktor.sessions.sessionOrNull
import java.util.*

fun Route.login(repo: UserRepository, hash: (String) -> String) {
    get<Login> {
        val user = call.sessionOrNull<Session>()?.let { repo.findOne(it.id) }

        if (user != null) {
            //TODO redirect to user page
            call.redirect("/")
        } else {
            call.respond(FreeMarkerContent("login.ftl", Collections.EMPTY_MAP, ""))
        }
    }
    post<Login> {
        val login = when {
            it.username.length < 4 -> null
            it.password.length < 6 -> null
            !userNameValid(it.username) -> null
            else -> repo.findByUsernameAndPasswordHash(it.username, hash(it.password))
        }

        if (login == null) {
            call.redirect(it.copy(password = "", error = "Invalid username or password"))
        } else {
            call.session(Session(login.id))
            //TODO redirect to user page
            call.redirect("/")
        }

    }
}