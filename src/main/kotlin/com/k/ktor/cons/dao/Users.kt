package com.k.ktor.cons.dao

import com.k.ktor.cons.model.User
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select

object Users : Table() {
    val id = long("id").primaryKey()
    val username = varchar("username", 32)
    val email = varchar("email", 128).uniqueIndex()
    val displayName = varchar("display_name", 256)
    val passwordHash = varchar("password_hash", 64)
}

class UserRepository(val db: Database = Database.connect("jdbc:h2:mem:constructor", driver = "org.h2.Driver")) {

    init {
        db.transaction {
            Users
        }
    }

    fun saveUser(user: User) = db.transaction {
        Users.insert {
            it[Users.id] = user.id
            it[Users.username] = user.username
            it[Users.email] = user.email
            it[Users.displayName] = user.displayName
            it[Users.passwordHash] = user.passwordHash
        }
    }

    fun findOne(id: Long, hash: String? = null) = db.transaction {
        Users.select { Users.id.eq(id) }
                .mapNotNull {
                    if (hash == null || it[Users.passwordHash] == hash) {
                        User(id, it[Users.username], it[Users.email], it[Users.displayName], it[Users.passwordHash])
                    } else {
                        null
                    }
                }
                .singleOrNull()
    }

    fun findByEmail(email: String) = db.transaction {
        Users.select { Users.email.eq(email) }.singleOrNull()
    }

}
