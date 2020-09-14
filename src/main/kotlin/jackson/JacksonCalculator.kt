package com.mika.webclientsoap.jackson

import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.mika.webclientsoap.Calculator
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

class JacksonCalculator(
    val webClient: WebClient
): Calculator {
    val mapper = XmlMapper()
        .registerModule(KotlinModule())

    override fun sum(x: Int, y: Int): Mono<Int> {
        return webClient
            .post()
            .uri("/calculator")
            .contentType(MediaType.TEXT_XML)
            .body(BodyInserters.fromProducer(Mono.just(addEnvelope(x, y)), String::class.java))
            .accept(MediaType.TEXT_XML)
            .exchange()
            .flatMap { printAndExtractBody(it) }
            .map { soapMapper(it).body.sumResponse?.`return` }
    }

    override fun multiply(x: Int, y: Int): Mono<Int> {
        return webClient
            .post()
            .uri("/calculator")
            .contentType(MediaType.TEXT_XML)
            .body(BodyInserters.fromProducer(Mono.just(multiplyEnvelope(x, y)), String::class.java))
            .accept(MediaType.TEXT_XML)
            .exchange()
            .flatMap { printAndExtractBody(it) }
            .map { soapMapper(it).body.multiplyResponse?.`return` }
    }

    private fun printAndExtractBody(res: ClientResponse): Mono<String> {
        if (res.statusCode().isError) {
            return Mono.error(IllegalStateException("Got HTTP status code ${res.statusCode()}"))
        }
        return res.bodyToMono(String::class.java)
    }

    private fun soapMapper(message: String): Envelope {
        return mapper.readValue(message, Envelope::class.java)
    }

    private fun addEnvelope(x: Int, y: Int): String =
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

    private fun multiplyEnvelope(x: Int, y: Int): String =
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
