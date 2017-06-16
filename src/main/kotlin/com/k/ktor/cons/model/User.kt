package com.k.ktor.cons.model

import java.io.Serializable

/**
 * @since 13.06.17
 */
data class User(val id: Int?, val username: String, val email: String, val displayName: String, val passwordHash: String): Serializable
