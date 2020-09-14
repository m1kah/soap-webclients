package com.mika.webclientsoap.javax

import com.mika.webclientsoap.Calculator
import org.springframework.web.reactive.function.client.WebClient
import org.superbiz.wsdl.MultiplyResponse
import org.superbiz.wsdl.ObjectFactory
import org.superbiz.wsdl.SumResponse
import reactor.core.publisher.Mono

class JavaxCalculator(
    webClient: WebClient
): Calculator {
    val soapClient = JavaxSoapClient(webClient)
    val objectFactory = ObjectFactory()

    override fun sum(x: Int, y: Int): Mono<Int> {
        val sum = objectFactory.createSum()
        sum.arg0 = x
        sum.arg1 = y
        val req = objectFactory.createSum(sum)
        return soapClient.exchange(req, SumResponse::class.java).map { it.`return` }
    }

    override fun multiply(x: Int, y: Int): Mono<Int> {
        val multiply = objectFactory.createMultiply()
        multiply.arg0 = x
        multiply.arg1 = y
        val req = objectFactory.createMultiply(multiply)
        return soapClient.exchange(req, MultiplyResponse::class.java).map { it.`return` }
    }
}
