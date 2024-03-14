package com.example.observabilityWithKotlin.config

import com.example.observabilityWithKotlin.controller.FunctionEndpointController
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpMethod
import org.springframework.http.client.reactive.ClientHttpConnector
import org.springframework.http.client.reactive.ClientHttpRequest
import org.springframework.http.client.reactive.ClientHttpResponse
import reactor.core.publisher.Mono
import java.net.URI
import java.util.function.Function


class LoggingClientHttpConnectorDecorator(val delegate: ClientHttpConnector) : ClientHttpConnector {
    private val log: Logger = LoggerFactory.getLogger(FunctionEndpointController::class.java)

    override fun connect(
        method: HttpMethod,
        uri: URI,
        callback: Function<in ClientHttpRequest, Mono<Void>>
    ): Mono<ClientHttpResponse> {
        return delegate.connect(method, uri, Function { request ->
            callback.apply(LoggingClientHttpRequestDecorator(request))
        })
    }
}