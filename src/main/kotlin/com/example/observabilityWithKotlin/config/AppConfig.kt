package com.example.observabilityWithKotlin.config

import io.micrometer.common.KeyValue
import io.micrometer.common.KeyValues
import io.micrometer.core.instrument.Meter
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.config.MeterFilter
import io.micrometer.observation.ObservationHandler
import io.micrometer.observation.ObservationRegistry
import io.micrometer.observation.aop.ObservedAspect
import io.micrometer.observation.contextpropagation.ObservationThreadLocalAccessor
import kotlinx.coroutines.slf4j.MDCContext
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import org.slf4j.MDC

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.http.server.reactive.observation.DefaultServerRequestObservationConvention
import org.springframework.http.server.reactive.observation.ServerRequestObservationContext
import org.springframework.stereotype.Component
import org.springframework.web.server.CoWebFilter
import org.springframework.web.server.CoWebFilterChain
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter


@Configuration(proxyBeanMethods = false)
class AppConfig(
    private val observationHandlers: Set<ObservationHandler<*>>,
    private val meterRegistry: MeterRegistry,
) {
    private val logger = LoggerFactory.getLogger(AppConfig::class.java)

    @Bean
    fun observedAspect(observationRegistry: ObservationRegistry): ObservedAspect {
        val observationConfig = observationRegistry.observationConfig().observationPredicate{ t, u ->
            !t.contains("scheduled") //ScheduledTaskObservationContext
        }
        observationHandlers.forEach(observationConfig::observationHandler)
        meterRegistry.config()
            .meterFilter(MeterFilter.deny { id: Meter.Id ->
                val name = id.name
                val function = id.getTag("code.function")
                if(function == "generateThreadDump") {
                    true
                }
                else false
                //name.startsWith("tasks") || name.contains("scheduled") || name == "tasks.scheduled.execution"
            })
        ObservationThreadLocalAccessor.getInstance().observationRegistry = observationRegistry
        return ObservedAspect(observationRegistry)
    }

    @Bean
    fun coroutineWebFilter(): WebFilter {
        return object : CoWebFilter() {
            override suspend fun filter(exchange: ServerWebExchange, chain: CoWebFilterChain)
                    = withContext(MDCContext()) {
                chain.filter(exchange)
            }
        }
    }
}

//@Configuration
//class LogbackConfiguration {
//    @Bean
//    fun mdcAsListConverter(): ClassicConverter {
//        return MdcAsListConverter()
//    }
//}

//@Component
//class FsiObservationConvention(env : Environment) : DefaultServerRequestObservationConvention() {
//
//    private val logger = LoggerFactory.getLogger(FsiObservationConvention::class.java)
//
//    init {
//        var activeProfiles: Array<String> = env.activeProfiles
//        // If there are no active profiles, default profiles are returned
//        // If there are no active profiles, default profiles are returned
//        if (activeProfiles.isEmpty()) {
//            activeProfiles = env.defaultProfiles
//        }
//        logger.info("Initializing FsiObservationConvention on profile - ${activeProfiles.toList()}")
//    }
//    override fun getLowCardinalityKeyValues(context: ServerRequestObservationContext): KeyValues {
//        return super.getLowCardinalityKeyValues(context).and(additionalTags(context))
//    }
//
//    private fun additionalTags(context: ServerRequestObservationContext): KeyValues {
////        val headers = context.carrier.headers
////
////        headers.forEach { (key, values) ->
////            val headerKey = "Header_$key"
////
////            values.forEachIndexed { index, value ->
//////                if("$headerKey-$index"=="Myheader") {
////                    println("Comingggg"+"$headerKey-$index")
////                    MDC.put("$headerKey-$index", value)
//////                    println("Values here " + value)
//////                }
////            }
////        }
////        return KeyValues.empty()
//
//        var keyValues = KeyValues.empty()
//        val headerKey = "Myheader"
//        val header = context.carrier.headers[headerKey]?.first()
//        if (header != null) {
//            keyValues = keyValues.and(KeyValue.of(headerKey, header))
//            //println(keyValues)
//            MDC.put(headerKey, header)
//        }
//
//        return keyValues
//    }
//
//}

@Component
class CustomLoggerAppender(env: Environment) : DefaultServerRequestObservationConvention() {

    private val logger = LoggerFactory.getLogger(CustomLoggerAppender::class.java)

    override fun getLowCardinalityKeyValues(context: ServerRequestObservationContext): KeyValues {
        return super.getLowCardinalityKeyValues(context).and(additionalTags(context))
    }

    private fun additionalTags(context: ServerRequestObservationContext): KeyValues {
        var keyValues = KeyValues.empty()
        val headerKey = "Myheader"
        val header = context.carrier.headers[headerKey]?.firstOrNull()
        MDC.clear();
        if (header != null) {
            keyValues = keyValues.and(KeyValue.of(headerKey, header))
            MDC.put(headerKey, header)
        }

        return keyValues
    }
}

