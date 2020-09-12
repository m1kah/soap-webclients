package com.mika.webclientsoap

import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.module.kotlin.KotlinModule
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@ExtendWith(SpringExtension::class)
@SpringBootTest
class WebClientSoapTest {
    @Autowired
    lateinit var webClient: WebClient
    val mapper = XmlMapper()
        .registerModule(KotlinModule())

    @Test
    fun add() {
        val res = webClient
            .post()
            .uri("/calculator")
            .body(BodyInserters.fromProducer(Mono.just(addEnvelope(12, 9)), String::class.java))
            .exchange().block()!!
        val body = printAndExtractBody(res)
        assertEquals(200, res.rawStatusCode())

        val returnValue = soapMapper(body).body.sumResponse?.`return`
        assertEquals(12 + 9, returnValue)
    }

    @Test
    fun multiply() {
        val res = webClient
            .post()
            .uri("/calculator")
            .body(BodyInserters.fromProducer(Mono.just(multiplyEnvelope(12, 9)), String::class.java))
            .exchange().block()!!
        val body = printAndExtractBody(res)
        assertEquals(200, res.rawStatusCode())

        val returnValue = soapMapper(body).body.multiplyResponse?.`return`
        assertEquals(12 * 9, returnValue)
    }

    private fun printAndExtractBody(res: ClientResponse): String {
        val body = res.bodyToMono(String::class.java).block()
        log.info("${res.statusCode()}")
        log.info(body)
        return body ?: ""
    }

    fun soapMapper(message: String): Envelope {
        return mapper.readValue(message, Envelope::class.java)
    }

    fun addEnvelope(x: Int, y: Int): String =
        """
            <SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/">
                <SOAP-ENV:Header/>
                <SOAP-ENV:Body>
                    <ns2:sum xmlns:ns2="http://superbiz.org/wsdl">
                        <arg0>$x</arg0>
                        <arg1>$y</arg1>
                    </ns2:sum>
                </SOAP-ENV:Body>
            </SOAP-ENV:Envelope>
        """.trimIndent()

    fun multiplyEnvelope(x: Int, y: Int): String =
        """
            <SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/">
                <SOAP-ENV:Header/>
                <SOAP-ENV:Body>
                    <ns2:multiply xmlns:ns2="http://superbiz.org/wsdl">
                        <arg0>$x</arg0>
                        <arg1>$y</arg1>
                    </ns2:multiply>
                </SOAP-ENV:Body>
            </SOAP-ENV:Envelope>
        """.trimIndent()

    companion object {
        val log = LoggerFactory.getLogger(WebClientSoapTest::class.java)
    }
}

data class Envelope(
    @JacksonXmlProperty(localName = "Body")
    val body: Body
)

data class Body(
    val sumResponse: Response?,
    val multiplyResponse: Response?
)

data class Response(
    val `return`: Int
)
