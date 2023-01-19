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
import org.springframework.test.web.servlet.post

class TreeControllerTest : AbstractTest() {

    @Test
    @Sql(statements = ["DELETE FROM ${TableNames.TREE_TABLE}"], executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
    fun `create tree should return 200`() {
        val user = givenTestUser("any@mail.io", "password")

        mockMvc.post("/api/v1/tree") {
            withAuthenticationAs(user)

            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.createObjectNode().apply {
                put("latitude", "0.0")
                put("longitude", "0.0")
            }
        }.andExpect {
            status { isOk() }
            jsonPath("treeId") { isNotEmpty() }
        }
    }

    @Test
    fun `get tree by id should return 200`() {
        val user = givenTestUser(email = "any@mail.io", password = "password")
        val tree = givenTree(userId = user.id, latitude = 0.0, longitude = 0.0)

        mockMvc.get("/api/v1/tree/${tree.id}")
            .andExpect {
                status { isOk() }
                jsonPath("id") { value(tree.id.toString()) }
            }
    }

    @Test
    fun `delete tree should return 200`() {
        val user = givenTestUser(email = "any@mail.io", password = "password")
        val tree = givenTree(userId = user.id, latitude = 0.0, longitude = 0.0)

        mockMvc.delete("/api/v1/tree/${tree.id}") {
            withAuthenticationAs(user)
        }.andExpect {
            status { isOk() }
        }
    }

    @Test
    fun `get all trees should return 200`() {
        val user = givenTestUser(email = "any@mail.io", password = "password")
        repeat((0..25).count()) { givenTree(userId = user.id, latitude = 0.0, longitude = 0.0) }

        mockMvc.get("/api/v1/tree/all/1/20") {
            withAuthenticationAs(user)
        }.andExpect {
            status { isOk() }
            jsonPath("$[*]") { hasSize(20) }
        }
    }

    @Test
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