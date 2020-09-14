package com.mika.webclientsoap.javax

import org.reactivestreams.Publisher
import org.springframework.core.io.ByteArrayResource
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DefaultDataBufferFactory
import org.springframework.http.MediaType
import org.springframework.web.reactive.function.BodyExtractors
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import org.superbiz.wsdl.Multiply
import org.superbiz.wsdl.MultiplyResponse
import org.superbiz.wsdl.Sum
import org.superbiz.wsdl.SumResponse
import reactor.core.publisher.Mono
import reactor.core.publisher.toMono
import java.io.*
import java.lang.IllegalStateException
import java.nio.ByteBuffer
import javax.xml.bind.JAXBContext
import javax.xml.bind.JAXBElement
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.soap.MessageFactory
import javax.xml.soap.SOAPMessage

class JavaxSoapClient(
    val webClient: WebClient
) {
    val messageFactory = MessageFactory.newInstance()
    val jaxbContext = JAXBContext.newInstance(
        Sum::class.java,
        Multiply::class.java,
        SumResponse::class.java,
        MultiplyResponse::class.java)
    val documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()

    fun <T> exchange(body: JAXBElement<*>, responseType: Class<T>): Mono<T> {
        return webClient
            .post()
            .uri("/calculator")
            .contentType(MediaType.TEXT_XML)
            .body(BodyInserters.fromDataBuffers(makeMessageAndSerialize(body)))
            .accept(MediaType.TEXT_XML)
            .exchange()
            .flatMap {
                if (it.statusCode().is2xxSuccessful) {
                    it.bodyToMono(ByteArrayResource::class.java)
                        .map { resource -> deserializeBody(resource.inputStream, responseType) }
                } else {
                    Mono.error<T>(IllegalStateException("Got HTTP status ${it.rawStatusCode()}"))
                }
            }
    }

    fun makeMessageAndSerialize(body: JAXBElement<*>): Publisher<DataBuffer> {
        return Mono.just(body)
                .map { serialize(soapMessage(it)) }
                .map { DefaultDataBufferFactory().wrap(ByteBuffer.wrap(it)) }
    }

    private fun soapMessage(body: JAXBElement<*>): SOAPMessage {
        val message = messageFactory.createMessage()
        val marshaller = jaxbContext.createMarshaller()
        val document = documentBuilder.newDocument()
        marshaller.marshal(body, document)
        message.soapBody.addDocument(document)
        message.saveChanges()
        return message;
    }

    private fun serialize(message: SOAPMessage): ByteArray {
        val out = ByteArrayOutputStream()
        message.writeTo(out)
        return out.toByteArray()
    }

    private fun <T> deserializeBody(dataIs: InputStream, type: Class<T>): T {
        val message = messageFactory.createMessage(null, dataIs)
        val unmarshaller = jaxbContext.createUnmarshaller()
        return unmarshaller.unmarshal(message.soapBody.extractContentAsDocument(), type).value
    }
}
