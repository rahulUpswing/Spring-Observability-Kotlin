package com.example.observabilityWithKotlin.controller

import com.example.observabilityWithKotlin.service.AppService
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api")
class FunctionEndpointController(private val catFactService: AppService) {
    private val log: Logger = LoggerFactory.getLogger(FunctionEndpointController::class.java)

    @GetMapping("/test1")
    suspend fun testEndpoint1(): String? {
        log.info("Hitting api test1")
        return catFactService.getCatFact()
    }
    @PostMapping("/test2")
    suspend fun postTest(@RequestBody message: String): String {
        log.info("Hitting api test2")
        return catFactService.postRequest(message)
    }
}