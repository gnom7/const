package com.k.ktor.cons.model

import org.joda.time.DateTime
import java.io.Serializable

/**
 * @since 13.06.17
 */
data class Tweet(val id: Long, val userId: String, val text: String, val date: DateTime, val replyTo: Int?): Serializable
