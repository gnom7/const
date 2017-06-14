package com.k.ktor.cons

import org.jetbrains.ktor.application.Application
import org.jetbrains.ktor.http.HttpMethod
import org.jetbrains.ktor.http.HttpStatusCode
import org.jetbrains.ktor.testing.handleRequest
import org.jetbrains.ktor.testing.withTestApplication
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class AppTest {

    @Test fun testApp() = withTestApplication(Application::main) {

        with(handleRequest(HttpMethod.Get, "/")) {
            assertEquals(HttpStatusCode.OK, response.status())
            assertEquals("Hey", response.content)
        }

        with(handleRequest(HttpMethod.Get, "/grdgdg")) {
            assertFalse(requestHandled)
        }

        with(handleRequest(HttpMethod.Get, "/styles/main.css")) {
            assertTrue(requestHandled)
        }
    }

}
