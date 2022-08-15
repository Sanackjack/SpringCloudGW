package com.ss.springcloudgw.filter;


import com.ss.springcloudgw.constant.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.UUID;

@Component
@Slf4j
public class CorrelationConfigFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.debug("Correlation GlobalFilter executed");

        String xCorrelationId = exchange.getRequest().getHeaders().getFirst(Constants.HEADER_CORRELATION_ID);
        String correlationId = StringUtils.isNotEmpty(xCorrelationId)
                ? xCorrelationId
                : Constants.NAMESPACE + UUID.randomUUID().toString().replaceAll("-","").toLowerCase().trim();
        exchange.getAttributes().put(Constants.CORRELATION_ID, correlationId);
        exchange.getRequest().mutate().header(Constants.HEADER_CORRELATION_ID, correlationId).build();

        String clientIp = Objects.requireNonNull(exchange.getRequest().getRemoteAddress()).toString();
        exchange.getAttributes().put(Constants.CLIENT_IP, clientIp);

        ThreadContext.put(Constants.CORRELATION_ID, correlationId);

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE; // set for use first filter
    }

}
