package io.citytrees.controller

import io.citytrees.AbstractTest
import io.citytrees.constants.CookieNames
import io.citytrees.v1.model.UserGetResponse
import io.citytrees.v1.model.UserRegisterResponse
import io.citytrees.v1.model.UserRole
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put
import java.util.*
import kotlin.test.assertEquals

class UserControllerTest : AbstractTest() {

    @Test
    fun `user register should return 200`() {
        val email = "test@example.com"
        val password = "pass"

        val content = mockMvc.post("/api/v1/user/register") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.createObjectNode().apply {
                put("email", email)
                put("password", password)
            }
        }.andExpect {
            status { isOk() }
            jsonPath(UserRegisterResponse::userId.name) { isNotEmpty() }
        }.andReturn().response.contentAsString

        val response = objectMapper.readValue(content, UserRegisterResponse::class.java)
        userRepository.deleteById(response.userId)
    }

    @Test
    fun `user get by id should return 200`() {
        val user = givenTestUser(
            email = "test@example.com",
            firstName = "FirstName",
            lastName = "LastName",
            password = "p@ssw0rd!",
            roles = setOf(UserRole.BASIC),
        )

        mockMvc.get("/api/v1/user/${user.id}")
            .andExpect {
                status { isOk() }
                jsonPath(UserGetResponse::id.name) { value(user.id.toString()) }
                jsonPath(UserGetResponse::email.name) { value(user.email) }
                jsonPath(UserGetResponse::roles.name) { isArray() }
                jsonPath(UserGetResponse::roles.name) { hasSize(1) }
                jsonPath(UserGetResponse::roles.name + "[0]") { value(UserRole.BASIC.name) }
                jsonPath(UserGetResponse::firstName.name) { value(user.firstName) }
                jsonPath(UserGetResponse::lastName.name) { value(user.lastName) }
            }
    }

    @Test
    fun `user update by id should return 200`() {
        val userId = UUID.randomUUID()
        val user = givenTestUser(
            id = userId,
            email = "test@example.com",
            firstName = "FirstName",
            lastName = "LastName",
            password = "p@ssw0rd!",
        )

        val newEmail = "newexample@mail.com"
        val newFirstName = "NewFirstName"
        val newLastName = "NewLastName"

        mockMvc.put("/api/v1/user/${userId}") {
            withAuthenticationAs(user)
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.createObjectNode().apply {
                put("email", newEmail)
                put("firstName", newFirstName)
                put("lastName", newLastName)
            }
        }.andExpect {
            status { isOk() }
        }

        userService.getById(userId)
            .ifPresent {
                assertEquals(it.email, newEmail)
                assertEquals(it.firstName, newFirstName)
                assertEquals(it.lastName, newLastName)
            }
    }

    @Test
    fun `user update password should return 200`() {
        val email = "test@example.com"
        val password = "123"
        givenTestUser(email, password)
        val newPassword = "1234"

        val accessToken = mockMvc.post("/api/v1/auth/basic") { withBasicAuthHeader(email, password) }
            .andExpect { status { isOk() } }
            .andReturn()
            .response
            .getCookie(CookieNames.ACCESS_TOKEN)
            ?.value
            ?: error("Cookie is not presented")

        mockMvc.put("/api/v1/user/password") {
            withAccessTokenAuthHeader(accessToken)
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.createObjectNode().apply {
                put("newPassword", newPassword)
            }
        }.andExpect { status { isOk() } }


        mockMvc.post("/api/v1/auth/basic") {
            withBasicAuthHeader(email, newPassword)
        }.andExpect {
            status { isOk() }
        }
    }
}
