package com.example.observabilityWithKotlin.service

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import org.springframework.web.reactive.function.client.awaitExchange

@Service
class AppService(private val webClientBuilder: WebClient.Builder) {
    private val log: Logger = LoggerFactory.getLogger(AppService::class.java)

    suspend fun getCatFact(): String? {
//        log.info("Api call")
        val webClient = webClientBuilder
            .baseUrl("https://catfact.ninja")
            .build()

        return webClient.get()
            .uri("/fact")
            .awaitExchange { it.awaitBody<String>() }
    }

}
