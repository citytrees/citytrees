package io.citytrees.controller

import io.citytrees.AbstractTest
import io.citytrees.repository.UserRepository
import io.citytrees.util.HashUtil
import io.citytrees.v1.model.UserGetById200Response
import io.citytrees.v1.model.UserRegisterNew200Response
import io.citytrees.v1.model.UserRole
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put
import java.util.*
import kotlin.test.assertEquals
import kotlin.test.assertNull

class UserControllerTest : AbstractTest() {

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var hashUtil: HashUtil

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
        }.andReturn().apply {
            val response = objectMapper.readValue(response.contentAsString, UserRegisterNew200Response::class.java)
            val user = userRepository.findByUserId(response.userId).orElseThrow()

            assertEquals(email, user.email)
            assertEquals(hashUtil.md5WithSalt(password), user.password)
            assertEquals(setOf(UserRole.BASIC), user.roles)
            assertNull(user.firstName)
            assertNull(user.lastName)
        }
    }

    @Test
    fun `user get by id should return 200`() {
        val user = givenTestUser(
            email = "test@example.com",
            firstName = "FirstName",
            lastName = "LastName",
            password = "p@ssw0rd!",
        )

        mockMvc.get("/api/v1/user/${user.id}")
            .andExpect { status { isOk() } }
            .andReturn().apply {
                val response = objectMapper.readValue(response.contentAsString, UserGetById200Response::class.java)
                assertEquals(user.id, response.id)
                assertEquals(user.email, response.email)
                assertEquals(user.roles, response.roles.toSet())
                assertEquals(user.firstName, response.firstName)
                assertEquals(user.lastName, response.lastName)
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
