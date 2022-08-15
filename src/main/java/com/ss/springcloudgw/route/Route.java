package com.ss.springcloudgw.route;


import com.ss.springcloudgw.configs.RouteConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@RequiredArgsConstructor
public class Route {

    private final RouteConfig routeConfig;
    private final RedisRateLimiter redisRateLimiter;
    private final KeyResolver rateLimiterKeyResolver;

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {

        System.out.println("test"+routeConfig.getRouteApi());

        return builder.routes()
                .route("ss-api", r -> r
                        .path("/otp/mobile"
                                , "/otp/web"
                                ,"/api/v1/test")

                        .filters(f -> f
                                .circuitBreaker(c -> c.setName("testCircuit")
                                        .setFallbackUri("forward:/fallback"))
                                .requestRateLimiter(c -> c.setRateLimiter(redisRateLimiter)
                                        .setKeyResolver(rateLimiterKeyResolver))
                        ) .uri(routeConfig.getRouteApi()))

                .build();
    }

}
