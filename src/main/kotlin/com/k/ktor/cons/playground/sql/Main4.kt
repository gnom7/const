package com.k.ktor.cons.playground.sql

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.SchemaUtils.createMissingTablesAndColumns
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

object Users : IntIdTable(name = "USERS") {
    val username = varchar("username", 32).index(isUnique = true)
    val city = reference("city", Cities)
    val age = integer("age")
}

object Cities : IntIdTable(name = "CITIES") {
    val name = varchar("name", 64)
}

class User(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<User>(Users)

    var username by Users.username
    var city by City referencedOn Users.city
    var age by Users.age
}

class City(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<City>(Cities)

    var name by Cities.name
    val users by User referrersOn Users.city
}

fun main(args: Array<String>) {
    val dbFilePath = File("target/db").absolutePath//.let { File("$it.mv.db").delete(); it }
    Database.connect("jdbc:h2:file:$dbFilePath", driver = "org.h2.Driver")

    transaction {
        create(Cities, Users)
    }

    transaction {
        logger.addLogger(StdOutSqlLogger)

//        create (Cities, Users)

        val stPete = City.new {
            name = "St. Petersburg"
        }

        val munich = City.new {
            name = "Munich"
        }

        User.new {
            username = "a"
            city = stPete
            age = 5
        }

        User.new {
            username = "b"
            city = stPete
            age = 27
        }

        User.new {
            username = "c"
            city = munich
            age = 42
        }

        println("Cities: ${City.all().joinToString { it.name }}")
        println("Users in ${stPete.name}: ${stPete.users.joinToString { it.username }}")
        println("Adults: ${User.find { Users.age greaterEq 18 }.joinToString { it.username }}")
    }
}
