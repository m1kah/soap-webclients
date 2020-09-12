package com.mika.webclientsoap

import io.netty.channel.ChannelOption
import io.netty.handler.timeout.ReadTimeoutHandler
import io.netty.handler.timeout.WriteTimeoutHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient
import reactor.netty.tcp.TcpClient
import java.util.concurrent.TimeUnit

@Configuration
class CalculatorConfig {
    @Bean
    fun soapClient(): WebClient {
        return WebClient.builder()
            .baseUrl("http://localhost:8080/ws/")
            .defaultHeader("Content-Type", "text/xml")
            .clientConnector(
                ReactorClientHttpConnector(
                    HttpClient.from(
                        TcpClient.create()
                            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 1_000)
                            .doOnConnected {
                                it.addHandlerLast(ReadTimeoutHandler(500, TimeUnit.MILLISECONDS))
                                it.addHandlerLast(WriteTimeoutHandler(500, TimeUnit.MILLISECONDS))
                            })))
            .build()
    }
}