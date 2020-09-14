package com.mika.webclientsoap.javax

import com.mika.webclientsoap.CalculatorRequest
import com.mika.webclientsoap.CalculatorResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/javax")
class JavaxController(
    @Autowired val webClient: WebClient,
) {
    val calculator = JavaxCalculator(webClient)

    @PostMapping("/sum")
    fun sum(
        @RequestBody request: CalculatorRequest
    ): Mono<CalculatorResponse> =
        calculator.sum(request.x, request.y).map { CalculatorResponse(it) }

    @PostMapping("/multiply")
    fun multiply(
        @RequestBody request: CalculatorRequest
    ): Mono<CalculatorResponse> =
        calculator.multiply(request.x, request.y).map { CalculatorResponse(it) }
}
