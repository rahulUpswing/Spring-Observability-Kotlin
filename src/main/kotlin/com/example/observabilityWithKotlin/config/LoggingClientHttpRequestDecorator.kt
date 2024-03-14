package com.example.observabilityWithKotlin.config

import org.reactivestreams.Publisher
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.http.client.reactive.ClientHttpRequest
import org.springframework.http.client.reactive.ClientHttpRequestDecorator
import reactor.core.publisher.Mono


import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.nio.charset.StandardCharsets

class LoggingClientHttpRequestDecorator(delegate: ClientHttpRequest) : ClientHttpRequestDecorator(delegate) {
    private val log: Logger = LoggerFactory.getLogger(LoggingClientHttpRequestDecorator::class.java)



    private fun safelyLogRequestBody(dataBuffer: DataBuffer) {
        try {

            log.info("Request Body: ${dataBuffer.toString(StandardCharsets.UTF_8)}");
//            throw RuntimeException()
        } catch (e: Exception) {
            log.error("Error logging request body")
        }
    }

    override fun writeWith(body: Publisher<out DataBuffer>): Mono<Void> {
        return DataBufferUtils.join(body)
            .map { dataBuffer ->
                safelyLogRequestBody(dataBuffer)
            }
            .flatMap { dataBuffer ->
                Mono.just(dataBuffer)

            }
            .then(super.writeWith(body))

    }


}


