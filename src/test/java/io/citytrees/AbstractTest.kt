package io.citytrees

import com.fasterxml.jackson.databind.ObjectMapper
import io.citytrees.model.User
import io.citytrees.service.UserService
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockHttpServletRequestDsl
import org.springframework.test.web.servlet.MockMvc
import org.springframework.util.Base64Utils
import java.util.*
import kotlin.collections.ArrayDeque

@Tag("integration")
@SpringBootTest
@ActiveProfiles("dev")
@AutoConfigureMockMvc
abstract class AbstractTest {
    @Autowired
    protected lateinit var mockMvc: MockMvc

    @Autowired
    protected lateinit var objectMapper: ObjectMapper

    @Autowired
    protected lateinit var userService: UserService

    @AfterEach
    protected fun cleanup() = CLEANUP_TASKS.forEach(Runnable::run)

    protected fun MockHttpServletRequestDsl.withBasicAuthHeader(email: String, password: String) {
        header(HttpHeaders.AUTHORIZATION, "Basic ${Base64Utils.encodeToUrlSafeString("$email:$password".encodeToByteArray())}")
    }

    protected fun givenTestUser(
        email: String,
        password: String,
        id: UUID = UUID.randomUUID(),
        roles: Set<User.Role> = setOf(User.Role.VOLUNTEER),
        firstName: String? = null,
        lastName: String? = null,
    ) = userService.create(
        User.builder()
            .id(id)
            .email(email)
            .password(password)
            .roles(roles)
            .firstName(firstName)
            .lastName(lastName)
            .build()
    ).also {
        CLEANUP_TASKS.addFirst { userService.drop(it) }
    }

    companion object {
        private val CLEANUP_TASKS = ArrayDeque<Runnable>()
    }
}
