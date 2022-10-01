package io.citytrees.controller

import io.citytrees.AbstractTest
import io.citytrees.v1.model.UserRole
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put
import java.util.*
import kotlin.test.assertEquals

class UserControllerTest : AbstractTest() {

    @Test
    @Sql(statements = ["DELETE FROM ct_user"], executionPhase = AFTER_TEST_METHOD)
    fun `user register should return 200`() {
        val email = "test@example.com"
        val password = "pass"

        mockMvc.post("/api/v1/user/register") {
            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.createObjectNode().apply {
                put("email", email)
                put("password", password)
            }
        }.andExpect {
            status { isOk() }
            jsonPath("userId") { isNotEmpty() }
        }
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
                jsonPath("id") { value(user.id.toString()) }
                jsonPath("email") { value(user.email) }
                jsonPath("roles") { isArray() }
                jsonPath("roles") { hasSize(1) }
                jsonPath("roles[0]") { value(UserRole.BASIC.name) }
                jsonPath("firstName") { value(user.firstName) }
                jsonPath("lastName") { value(user.lastName) }
            }
    }

    @Test
    fun `user update by id should return 200`() {
        val userId = UUID.randomUUID()
        givenTestUser(
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

}
