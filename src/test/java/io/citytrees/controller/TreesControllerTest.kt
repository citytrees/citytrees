package io.citytrees.controller

import io.citytrees.AbstractTest
import io.citytrees.v1.model.TreesGetResponseTree
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.get

class TreesControllerTest : AbstractTest() {
    @Test
    fun `trees by region should return 200`() {
        val user = givenTestUser(email = "any@mail.io", password = "password")
        val tree = givenTree(userId = user.id, latitude = 1.0, longitude = 1.0)

        mockMvc.get("/api/v1/trees") {
            withAuthenticationAs(user)

            param("x1", "2.0")
            param("y1", "0.0")
            param("x2", "0.0")
            param("y2", "2.0")
        }.andExpect {
            status { isOk() }
            jsonPath("$[0].${TreesGetResponseTree::id.name}") { value(tree.id.toString()) }
        }
    }

    @Test
    fun `clusters by region should return 200`() {
        val user = givenTestUser(email = "any@mail.io", password = "password")

        givenTree(userId = user.id, latitude = 56.84991032559211, longitude = 60.604153275489814)

        givenTree(userId = user.id, latitude = 56.825927617482584, longitude = 60.62431812286378)
        givenTree(userId = user.id, latitude = 56.82593606619665, longitude = 60.62474191188813)

        mockMvc.get("/api/v1/trees/clusters") {
            withAuthenticationAs(user)

            param("x1", "57.05268277918522")
            param("y1", "61.07025146484376")
            param("x2", "56.54812987984381")
            param("y2", "60.11032104492188")
        }.andExpect {
            status { isOk() }
            jsonPath("$[*]") { isNotEmpty() }
        }
    }
}