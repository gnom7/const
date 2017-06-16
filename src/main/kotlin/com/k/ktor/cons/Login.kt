package com.k.ktor.cons

import com.k.ktor.cons.dao.UserRepository
import com.k.ktor.cons.model.User
import org.jetbrains.ktor.application.call
import org.jetbrains.ktor.auth.NotAuthenticatedCause
import org.jetbrains.ktor.freemarker.FreeMarkerContent
import org.jetbrains.ktor.locations.get
import org.jetbrains.ktor.locations.post
import org.jetbrains.ktor.routing.Route
import org.jetbrains.ktor.sessions.session
import org.jetbrains.ktor.sessions.sessionOrNull

fun Route.login(repo: UserRepository, checkHash: (row: String, hash: String) -> Boolean) {
    get<Login> {
        val user = call.sessionOrNull<Session>()?.let { repo.findOne(it.id) }

        if (user != null) {
            //TODO redirect to user page
            call.redirect("/")
        } else {
            call.respond(FreeMarkerContent("login.ftl", mapOf("pageUser" to User(null, it.username, "", "", "")), ""))
        }
    }
    post<Login> {
        val rowPassword = it.password
        val user = when {
            it.username.length < 4 -> null
            it.password.length < 6 -> null
            !userNameValid(it.username) -> null
            else -> repo.findByUsername(it.username)?.let {
                if (checkHash(rowPassword, it.passwordHash)) {
                    it
                } else {
                    throw SecurityException(NotAuthenticatedCause.InvalidCredentials.toString())
                }
            }
        }

        if (user == null) {
            call.redirect(it.copy(password = "", error = "Invalid username or password"))
        } else {
            call.session(Session(user.id!!))
            //TODO redirect to user page
            call.redirect("/")
        }

    }
}