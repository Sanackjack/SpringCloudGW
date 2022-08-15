package com.ss.springcloudgw.filter;

import com.ss.springcloudgw.constant.Constants;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Optional;

@Component
@Slf4j
public class LogInfoFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.debug("Pre Logging GlobalFilter executed");
        setDefaultLogContext(exchange);

        ServerHttpRequest request = exchange.getRequest();
        long startTime = System.currentTimeMillis();
        exchange.getAttributes().put(Constants.START_TIME, startTime);

        logRequest(request);
        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            log.info("post Logging Detail");
            setDefaultLogContext(exchange);
            logResponses(exchange);
        }));
    }

    private void setDefaultLogContext(ServerWebExchange exchange) {
        ThreadContext.put(Constants.CORRELATION_ID, exchange.getAttribute(Constants.CORRELATION_ID));
        ThreadContext.put(Constants.CLIENT_IP, exchange.getAttribute(Constants.CLIENT_IP));
    }

    private void logRequest(ServerHttpRequest request) {
        URI requestURI = request.getURI();
        String scheme = requestURI.getScheme();
        HttpHeaders headers = request.getHeaders();
        MediaType contentType = headers.getContentType();
        long length = headers.getContentLength();

        log.info("Request IP: {}, Scheme: {}, Host: {}, Path: {}, Method: {}, Content-Type: {}, Content-Length: {}",
                request.getRemoteAddress(), scheme, requestURI.getHost(), requestURI.getPath(), request.getMethod(), contentType, length);
    }

    private void logResponses(ServerWebExchange exchange) {
        ServerHttpResponse origResponse = exchange.getResponse();
        HttpHeaders headers = origResponse.getHeaders();
        MediaType contentType = headers.getContentType();
        long length = headers.getContentLength();
        Optional<Long> startTime = Optional.ofNullable(exchange.getAttribute(Constants.START_TIME));
        Long executeTime = startTime.map(aLong -> (System.currentTimeMillis() - aLong)).orElse(0L);

        log.info("Response Http CODE: {}, Path: {}, Content-Length: {}, Cost: {} ms",
                origResponse.getStatusCode(), exchange.getRequest().getURI().getPath(), length, executeTime);
    }

    @Override
    public int getOrder() {
        return HIGHEST_PRECEDENCE + 1;
    }

}
