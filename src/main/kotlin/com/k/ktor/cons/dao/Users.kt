package com.k.ktor.cons.dao

import com.k.ktor.cons.model.User
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

object Users : Table() {
    val id = integer("id").primaryKey().autoIncrement()
    val username = varchar("username", 32).uniqueIndex()
    val email = varchar("email", 128).uniqueIndex()
    val displayName = varchar("display_name", 32).uniqueIndex()
    val passwordHash = varchar("password_hash", 64)
}

class UserRepository {

    init {
        transaction {
            create(Users)
        }
    }

    fun saveUser(user: User) = transaction {
        Users.insert {
            it[Users.username] = user.username
            it[Users.email] = user.email
            it[Users.displayName] = user.displayName
            it[Users.passwordHash] = user.passwordHash
        }.generatedKey ?: throw IllegalStateException("No generated key returned")
    }

    fun findOne(id: Int, hash: String? = null) = transaction {
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

    fun findByEmail(email: String) = transaction {
        Users.select { Users.email.eq(email) }.mapNotNull { rowToUser(it) }.singleOrNull()
    }

    fun findByUsername(username: String) = transaction {
        Users.select {
            Users.username.eq(username)
        }.mapNotNull { rowToUser(it) }.singleOrNull()
    }

    private fun rowToUser(row: ResultRow):User =
            User(row[Users.id], row[Users.username], row[Users.email], row[Users.displayName], row[Users.passwordHash])

}
