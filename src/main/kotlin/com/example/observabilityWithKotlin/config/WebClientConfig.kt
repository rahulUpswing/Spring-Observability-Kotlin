package com.example.observabilityWithKotlin.config

import org.reactivestreams.Publisher
import org.slf4j.LoggerFactory
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.core.io.buffer.DataBufferUtils
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.web.reactive.function.client.ClientResponse
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.nio.charset.StandardCharsets


class WebClientConfig {

    companion object {
        private val logger = LoggerFactory.getLogger(WebClientConfig::class.java)

        fun logResponse(): ExchangeFilterFunction {
            return ExchangeFilterFunction.ofResponseProcessor { response ->
                logResponseBody(response)
            }
        }

        private fun safelyLogResponse(response: ClientResponse, body: String) {
            try {
                logger.info("Response Status: ${response.statusCode()}")
                logger.info("Response Body: $body")
//                                throw RuntimeException("Error 1")


            } catch (e: Exception) {
                logger.error("Error during response logging")
                // Error is handled and not propagated

            }
        }

        private fun logResponseBody(response: ClientResponse): Mono<ClientResponse> {
            return if (!response.statusCode().isError) {
                response.bodyToMono(String::class.java)
                    .flatMap { body ->
                        safelyLogResponse(response, body)
                        Mono.just(
                            ClientResponse.create(response.statusCode(), response.strategies())
                                .headers { headers -> headers.addAll(response.headers().asHttpHeaders()) }
                                .body(body)
                                .build()
                        )
                    }
            } else {
                Mono.just(response)
            }
        }

    }
}
