package com.example.observabilityWithKotlin.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.web.reactive.function.client.WebClient
import reactor.netty.http.client.HttpClient


@Configuration
class WebConfig {
//@Bean
//fun webClientBuilder(): WebClient.Builder? {
//    return WebClient.builder().filter(WebClientConfig.logResponse())
//}

    @Bean
    fun webClient(webClientBuilder: WebClient.Builder): WebClient {
        val client = HttpClient.create()
        val connector = ReactorClientHttpConnector(client)
        return webClientBuilder.filter(WebClientConfig.logResponse()).clientConnector(LoggingClientHttpConnectorDecorator(connector)).build()
    }
}
