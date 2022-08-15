package com.ss.springcloudgw.configs;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Slf4j
@Configuration
@ConfigurationProperties(prefix = "rate-limit")
@Setter
public class RateLimiterConfig {

    private int replenishRate;
    private int burstCapacity;

    @Bean
    public RedisRateLimiter redisRateLimiter() {
        return  new RedisRateLimiter(replenishRate,burstCapacity);
    }

    @Bean
    public KeyResolver rateLimiterKeyResolver() {
        return new KeyResolver() {
            @Override
            public Mono<String> resolve(ServerWebExchange exchange) {

                String ip = Objects.requireNonNull(exchange.getRequest().getRemoteAddress()).toString();
                log.info("KeyResolver: {}", ip);
                return Mono.just(ip);
            }
        };
    }


}
