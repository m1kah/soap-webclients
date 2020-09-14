package com.mika.webclientsoap

import io.restassured.RestAssured
import io.restassured.RestAssured.given
import io.restassured.http.ContentType
import org.hamcrest.CoreMatchers.equalTo
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.web.reactive.function.client.WebClient

@ExtendWith(SpringExtension::class)
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
class JacksonTest {
    @LocalServerPort
    var port: Int = 0

    @BeforeEach
    fun setup() {
        RestAssured.port = port
    }

    @Test
    fun sum() {
        given()
            .contentType(ContentType.JSON)
            .body(CalculatorRequest(13, 9))
            .accept(ContentType.JSON)
            .post("/jackson/sum")
        .then()
            .log().ifValidationFails()
            .statusCode(200)
            .body("value", equalTo(22))
    }

    @Test
    fun multiply() {
        given()
            .contentType(ContentType.JSON)
            .body(CalculatorRequest(13, 9))
            .accept(ContentType.JSON)
            .post("/jackson/multiply")
        .then()
            .log().ifValidationFails()
            .statusCode(200)
            .body("value", equalTo(117))
    }

}
