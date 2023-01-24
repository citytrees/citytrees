package io.citytrees.controller

import io.citytrees.AbstractTest
import io.citytrees.v1.model.FileGetResponse
import io.citytrees.v1.model.FileUploadResponse
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.multipart

class FileControllerTest : AbstractTest() {

    @Test
    fun `file upload should return 200`() {
        val user = givenTestUser("mail@example.com", "password")
        val mockFile = MockMultipartFile(
            "file",
            "test.txt",
            MediaType.TEXT_PLAIN_VALUE,
            "some content".toByteArray()
        )
        val content = mockMvc.multipart("/api/v1/file/upload") {
            withAuthenticationAs(user)
            contentType = MediaType.MULTIPART_FORM_DATA
            file(mockFile)
        }.andExpect {
            status { isOk() }
        }.andReturn().response.contentAsString

        val response = objectMapper.readValue(content, FileUploadResponse::class.java)
        fileService.delete(response.fileId)
    }

    @Test
    fun `file get by id should return 200`() {
        withSpringSecurityAuthentication(givenTestUser("mail@example.com", "password"))
        val file = givenCtFile()

        mockMvc.get("/api/v1/file/${file.id}")
            .andExpect {
                status { isOk() }
                jsonPath(FileGetResponse::id.name) { value(file.id.toString()) }
                jsonPath(FileGetResponse::name.name) { value(file.name) }
                jsonPath(FileGetResponse::mimeType.name) { value(file.mimeType) }
                jsonPath(FileGetResponse::size.name) { value(file.size) }
                jsonPath(FileGetResponse::hash.name) { value(file.hash) }
                jsonPath(FileGetResponse::userId.name) { value(file.userId.toString()) }
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