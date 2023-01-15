package io.citytrees.controller

import io.citytrees.AbstractTest
import io.citytrees.constants.TableNames
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.multipart

class FileControllerTest : AbstractTest() {

    @Test
    @Sql(statements = ["DELETE FROM ${TableNames.FILE_TABLE}"], executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    fun `file upload should return 200`() {
        val user = givenTestUser("mail@example.com", "password")
        val mockFile = MockMultipartFile(
            "file",
            "test.txt",
            MediaType.TEXT_PLAIN_VALUE,
            "some content".toByteArray()
        )
        mockMvc.multipart("/api/v1/file/upload") {
            withAuthenticationAs(user)
            contentType = MediaType.MULTIPART_FORM_DATA
            file(mockFile)
        }.andExpect {
            status { isOk() }
        }
    }

    @Test
    fun `file get by id should return 200`() {
        withSpringSecurityAuthentication(givenTestUser("mail@example.com", "password"))
        val file = givenCtFile()

        mockMvc.get("/api/v1/file/${file.id}")
            .andExpect {
                status { isOk() }
                jsonPath("id") { value(file.id.toString()) }
                jsonPath("name") { value(file.name) }
                jsonPath("mimeType") { value(file.mimeType) }
                jsonPath("size") { value(file.size) }
                jsonPath("hash") { value(file.hash) }
                jsonPath("userId") { value(file.userId.toString()) }
            }
    }

    @Test
    fun `file delete by id should return 200`() {
        val user = givenTestUser("mail@example.com", "password")
        withSpringSecurityAuthentication(user)
        val file = givenCtFile()
        mockMvc.delete("/api/v1/file/${file.id}") {
            withAuthenticationAs(user)
        }.andExpect {
            status { isOk() }
        }
    }

    @Test
    fun `file download id should return 200`() {
        withSpringSecurityAuthentication(givenTestUser("mail@example.com", "password"))
        val content = "example text content"
        val file = givenCtFile(content = content.toByteArray())

        mockMvc.get("/api/v1/file/download/${file.id}")
            .andExpect {
                status { isOk() }
                content { string(content) }
            }
    }
}