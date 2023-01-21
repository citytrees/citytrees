package io.citytrees.controller

import io.citytrees.AbstractTest
import io.citytrees.v1.model.TreeCreateResponse
import io.citytrees.v1.model.TreeGetResponse
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.multipart
import org.springframework.test.web.servlet.post

class TreeControllerTest : AbstractTest() {

    @Test
    fun `create tree should return 200`() {
        val user = givenTestUser("any@mail.io", "password")

        val content = mockMvc.post("/api/v1/tree") {
            withAuthenticationAs(user)

            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.createObjectNode().apply {
                put("latitude", "0.0")
                put("longitude", "0.0")
            }
        }.andExpect {
            status { isOk() }
            jsonPath(TreeCreateResponse::treeId.name) { isNotEmpty() }
        }.andReturn().response.contentAsString

        val response = objectMapper.readValue(content, TreeCreateResponse::class.java)
        treeRepository.deleteById(response.treeId)
    }

    @Test
    fun `get tree by id should return 200`() {
        val user = givenTestUser(email = "any@mail.io", password = "password")
        val tree = givenTree(userId = user.id, latitude = 0.0, longitude = 0.0)

        mockMvc.get("/api/v1/tree/${tree.id}")
            .andExpect {
                status { isOk() }
                jsonPath(TreeGetResponse::id.name) { value(tree.id.toString()) }
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

        mockMvc.get("/api/v1/tree/all/20/3") {
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

    @Test
    fun test1() {
        val user = givenTestUser(email = "any@mail.io", password = "password")
        repeat(50) {
            givenTree(userId = user.id, latitude = 0.0, longitude = 0.0)
        }
    }
}