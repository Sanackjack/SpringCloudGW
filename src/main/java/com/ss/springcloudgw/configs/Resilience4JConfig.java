package com.ss.springcloudgw.configs;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@ConfigurationProperties(prefix = "circuit-breaker")
@Setter
public class Resilience4JConfig {

    private int timeoutDuration;
    private int numberOfCalls;
    private int rateThreshold;


//    timeout-duration: 60   ช่วง time out ให้ check  Circuit breaker ทุก 60 วินาที
//    number-of-calls: 10    ให้คิดว้่ตีเป็น 10 ช่วง   (|1|2|3|4|5……|10|)  ของ timeout duration ทั้งหมด
//    rate-threshold: 40     คือประมาณค่าของเป็น % ของ error ที่ไว้เอาเชค เทียบ100 % คู่ number of call

    //https://resilience4j.readme.io/docs/circuitbreaker
    //https://docs.spring.io/spring-cloud-circuitbreaker/docs/current/reference/html/#default-configuration
    //https://docs.spring.io/spring-cloud-circuitbreaker/docs/current/reference/html/spring-cloud-circuitbreaker-resilience4j.html
    @Bean
    public Customizer<Resilience4JCircuitBreakerFactory> defaultCustomizer() {
        return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder("testCircuit")
                .timeLimiterConfig(TimeLimiterConfig.custom().timeoutDuration(Duration.ofSeconds(20)).build())
                .circuitBreakerConfig(CircuitBreakerConfig.custom()
                    .minimumNumberOfCalls(10)
                    .failureRateThreshold(1)
                    .build())
                .build());
    }

//    @Bean
//    public Customizer<ReactiveResilience4JCircuitBreakerFactory> defaultCustomizer() {
//        return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder("testCircuit")
//                .timeLimiterConfig(TimeLimiterConfig.custom().timeoutDuration(Duration.ofSeconds(timeoutDuration)).build())
//                .circuitBreakerConfig(CircuitBreakerConfig.custom()
//                    .minimumNumberOfCalls(numberOfCalls)
//                    .failureRateThreshold(rateThreshold)
//                    .build())
//                .build());
//    }

}
