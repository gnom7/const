package com.k.ktor.cons

import com.k.ktor.cons.dao.UserRepository
import com.k.ktor.cons.model.User
import org.jetbrains.ktor.application.call
import org.jetbrains.ktor.application.log
import org.jetbrains.ktor.freemarker.FreeMarkerContent
import org.jetbrains.ktor.locations.get
import org.jetbrains.ktor.locations.post
import org.jetbrains.ktor.routing.Route
import org.jetbrains.ktor.routing.application
import org.jetbrains.ktor.sessions.sessionOrNull

fun Route.register(repo: UserRepository, hashFunction: (String) -> String) {
    post<Register> {
        val user = call.sessionOrNull<Session>()?.let { repo.findOne(it.id) }
        if (user != null) {
            call.redirect("/")
        } else {
            val newUser = User(it.id, it.username, it.email, it.displayName, hashFunction(it.password))

            try {
                repo.saveUser(newUser)
            } catch (e: Exception) {
                if (repo.findOne(it.id) != null) {
                    call.redirect(it.copy(error = "User with the following login is already registered", password = ""))
                } else if (repo.findByEmail(it.email) != null) {
                    call.redirect(it.copy(error = "User with the following email ${it.email} is already registered", password = ""))
                } else {
                    application.log.error("Failed to register user", e)
                    call.redirect(it.copy(error = "Failed to register", password = ""))
                }
            }
        }
    }

    get<Register> {
        val user = call.sessionOrNull<Session>()?.let { repo.findOne(it.id) }
        if (user != null) {
            //TODO redirect to user's page
            call.redirect("/")
        } else {
            call.respond(FreeMarkerContent("register.ftl", mapOf("pageUser" to User(it.id, it.username, it.email, it.displayName, ""), "error" to it.error), ""))
        }
    }
}
