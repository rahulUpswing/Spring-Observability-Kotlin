package com.example.observabilityWithKotlin.controller


import com.example.observabilityWithKotlin.service.AppService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api")
class FunctionEndpointController(private val catFactService: AppService) {
    private val log: Logger = LoggerFactory.getLogger(FunctionEndpointController::class.java)

    @GetMapping("/test1")
    suspend fun testEndpoint1(): String? {
        log.info("Hitting api test1")
        return catFactService.getCatFact()
    }
}
