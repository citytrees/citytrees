package io.citytrees

import com.fasterxml.jackson.databind.ObjectMapper
import io.citytrees.constants.CookieNames
import io.citytrees.model.CtFile
import io.citytrees.model.User
import io.citytrees.repository.UserRepository
import io.citytrees.service.FileService
import io.citytrees.service.TokenService
import io.citytrees.service.UserService
import io.citytrees.v1.model.UserRole
import org.hamcrest.Matchers
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Tag
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockHttpServletRequestDsl
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.result.JsonPathResultMatchersDsl
import org.springframework.util.Base64Utils
import org.springframework.util.DigestUtils
import java.util.*
import javax.servlet.http.Cookie
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
    protected lateinit var userRepository: UserRepository

    @Autowired
    protected lateinit var userService: UserService

    @Autowired
    protected lateinit var fileService: FileService

    @Autowired
    protected lateinit var tokenService: TokenService

    @AfterEach
    protected fun cleanup() = CLEANUP_TASKS.forEach(Runnable::run)

    protected fun MockHttpServletRequestDsl.withBasicAuthHeader(email: String, password: String) =
        header(HttpHeaders.AUTHORIZATION, "Basic ${Base64Utils.encodeToUrlSafeString("$email:$password".encodeToByteArray())}")

    protected fun MockHttpServletRequestDsl.withAuthenticationAs(user: User) =
        header(HttpHeaders.AUTHORIZATION, "Bearer ${tokenService.generateNewPair(user).accessToken}")

    protected fun MockHttpServletRequestDsl.withAccessTokenAuthHeader(accessToken: String) =
        header(HttpHeaders.AUTHORIZATION, "Bearer $accessToken")

    protected fun MockHttpServletRequestDsl.withRefreshTokenCookie(refreshToken: String) =
        cookie(Cookie(CookieNames.REFRESH_TOKEN, refreshToken))

    protected fun JsonPathResultMatchersDsl.hasSize(size: Int) =
        value(Matchers.hasSize<Collection<*>>(size))

    protected fun givenTestUser(
        email: String,
        password: String,
        id: UUID = UUID.randomUUID(),
        roles: Set<UserRole> = setOf(UserRole.BASIC),
        firstName: String? = null,
        lastName: String? = null,
    ): User = User.builder()
        .id(id)
        .email(email)
        .password(password)
        .roles(roles)
        .firstName(firstName)
        .lastName(lastName)
        .build().also {
            userService.create(it)
            CLEANUP_TASKS.addFirst { userRepository.deleteById(it.id) }
        }

    protected fun givenCtFile(
        user: User,
        id: UUID = UUID.randomUUID(),
        name: String = "file",
        originalFilename: String = "test.txt",
        mediaType: String = MediaType.TEXT_PLAIN_VALUE,
        content: ByteArray = "some content".toByteArray(),
        saveToS3: Boolean = false
    ): CtFile = when (saveToS3) {
        false -> CtFile.builder()
            .id(id)
            .name(originalFilename)
            .mimeType(mediaType)
            .size(content.size.toLong())
            .hash(DigestUtils.md5DigestAsHex(content))
            .userId(user.id)
            .build()

        true -> fileService.saveToS3(
            MockMultipartFile(
                name,
                originalFilename,
                mediaType,
                content
            )
        )
    }.also {
        fileService.save(it)
        CLEANUP_TASKS.addFirst { fileService.delete(it.id) }
    }

    companion object {
        private val CLEANUP_TASKS = ArrayDeque<Runnable>()
    }
}
