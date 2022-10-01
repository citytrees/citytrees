package io.citytrees.controller

import io.citytrees.AbstractTest
import io.citytrees.constants.CookieNames.ACCESS_TOKEN
import io.citytrees.constants.CookieNames.REFRESH_TOKEN
import org.hamcrest.Matchers.emptyString
import org.hamcrest.Matchers.not
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.post
import java.time.Duration

class AuthControllerTest: AbstractTest() {

    @Test
    fun `should throw 400 when required header not exist`() {
        mockMvc.post("/api/v1/auth/basic")
            .andExpect {
                status { isEqualTo(400) }
            }
    }

    @Test
    fun `should throw 401 when user not exist`() {
        mockMvc.post("/api/v1/auth/basic") {
            withBasicAuthHeader("123@example.com", "123")
        }.andExpect {
            status { isEqualTo(401) }
        }
    }

    @Test
    fun `should return 200 and cookies when email and password matches`() {
        val email = "test@example.com"
        val password = "123"
        givenTestUser(email, password)

        mockMvc.post("/api/v1/auth/basic") {
            withBasicAuthHeader(email, password)
        }.andExpect {
            status { isOk() }
            cookie {
                value(ACCESS_TOKEN, not(emptyString()))
                maxAge(ACCESS_TOKEN, Duration.ofDays(365).toSeconds().toInt())
                value(REFRESH_TOKEN, not(emptyString()))
                maxAge(REFRESH_TOKEN, Duration.ofDays(365).toSeconds().toInt())
                httpOnly(REFRESH_TOKEN, true)
            }
        }
    }
}