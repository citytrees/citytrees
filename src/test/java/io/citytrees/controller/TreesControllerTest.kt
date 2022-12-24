package io.citytrees.controller

import io.citytrees.AbstractTest
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.post

class TreesControllerTest : AbstractTest() {
    @Test
    fun `trees by region should return 200`() {
        val user = givenTestUser(email = "any@mail.io", password = "password")
        val tree = givenTree(userId = user.id, latitude = 1.0, longitude = 1.0)

        mockMvc.post("/api/v1/trees") {
            withAuthenticationAs(user)

            contentType = MediaType.APPLICATION_JSON
            content = objectMapper.createObjectNode().apply {
                put("x1", 2.0)
                put("y1", 0.0)

                put("x2", 0.0)
                put("y2", 2.0)
            }
        }.andExpect {
            status { isOk() }
            jsonPath("$[0].id") { value(tree.id.toString()) }
        }
    }
}