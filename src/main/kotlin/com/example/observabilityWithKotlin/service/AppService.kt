package com.example.observabilityWithKotlin.service

import com.example.observabilityWithKotlin.config.LoggingClientHttpConnectorDecorator
import com.example.observabilityWithKotlin.config.WebClientConfig
//import com.example.observabilityWithKotlin.config.WebClientConfig
import kotlinx.coroutines.reactive.awaitSingle
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.http.client.reactive.ReactorClientHttpConnector
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import org.springframework.web.reactive.function.client.awaitExchange
import reactor.core.publisher.Mono
import reactor.netty.http.client.HttpClient



//@Service
//class AppService(private val webClient: WebClient) {
//
//        suspend fun getCatFact(): String? {
//        return webClient.get()
//            .uri("https://catfact.ninja/fact")
//            .awaitExchange {it.awaitBody<String>() }
//    }
//}

@Service
class AppService(private val webClientBuilder: WebClient
) {
    private val log: Logger = LoggerFactory.getLogger(AppService::class.java)


    suspend fun postRequest(jsonBody: String): String {


        return webClientBuilder.post()
            .uri("http://localhost:8086/test")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(jsonBody)
            .retrieve()
            .bodyToMono(String::class.java)
            .awaitSingle()
    }
    suspend fun getCatFact(): String? {
        return webClientBuilder.get()
            .uri("https://catfact.ninja/fact")
            .awaitExchange {it.awaitBody<String>() }
    }

}
