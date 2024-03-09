package com.example.observabilityWithKotlin

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import reactor.core.publisher.Hooks

@SpringBootApplication
class ObservabilityWithKotlinApplication

fun main(args: Array<String>) {
	Hooks.enableAutomaticContextPropagation();
	runApplication<ObservabilityWithKotlinApplication>(*args)
}
