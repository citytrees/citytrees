package io.citytrees.controller

import io.citytrees.AbstractTest
import io.citytrees.constants.TableNames
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.context.jdbc.Sql
import org.springframework.test.web.servlet.multipart

class TreeFilesControllerTest : AbstractTest() {
    @Test
    @Sql(statements = ["DELETE FROM ${TableNames.FILE_TABLE}"], executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    fun `attach file should return 200`() {
        val user = givenTestUser(email = "any@mail.io", password = "password")
        val tree = givenTree(userId = user.id, latitude = 0.0, longitude = 0.0)
        val mockFile = MockMultipartFile(
            "file",
            "test.txt",
            MediaType.TEXT_PLAIN_VALUE,
            "some content".toByteArray()
        )

        mockMvc.multipart("/api/v1/tree/${tree.id}/file") {
            withAuthenticationAs(user)
            contentType = MediaType.MULTIPART_FORM_DATA
            file(mockFile)
        }.andExpect {
            status { isOk() }
            jsonPath("$[*]") { isNotEmpty() }
        }
    }
}