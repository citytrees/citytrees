package io.citytrees.controller

import io.citytrees.AbstractTest
import io.citytrees.model.User
import io.citytrees.repository.UserRepository
import io.citytrees.util.HashUtil
import io.citytrees.v1.model.RegisterNewUser200Response
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD
import org.springframework.test.web.servlet.post
import kotlin.test.assertEquals
import kotlin.test.assertNull

class RegisterControllerTest : AbstractTest() {

    @Autowired
    private lateinit var userRepository: UserRepository

    @Autowired
    private lateinit var hashUtil: HashUtil

    @Test
    @Sql(statements = ["DELETE FROM ct_user"], executionPhase = AFTER_TEST_METHOD)
    fun shouldReturn200() {
        val email = "test@example.com"
        val password = "pass"

        mockMvc.post("/api/v1/register") {
            contentType = MediaType.APPLICATION_JSON
            content = givenJsonContent(email, password)
        }.andExpect {
            status { isOk() }
            jsonPath("userId") { isNotEmpty() }
        }.andReturn().apply {
            val response = objectMapper.readValue(response.contentAsString, RegisterNewUser200Response::class.java)
            val user = userRepository.findByUserId(response.userId).orElseThrow()

            assertEquals(email, user.email)
            assertEquals(hashUtil.md5WithSalt(password), user.password)
            assertEquals(setOf(User.Role.VOLUNTEER), user.roles)
            assertNull(user.firstName)
            assertNull(user.lastName)
        }
    }

    @Test
    fun willFail() {
        throw Exception("")
    }

    private fun givenJsonContent(email: String, password: String) = """
        {
            "email": "$email",
            "password": "$password"
        }
    """.trimIndent()
}
