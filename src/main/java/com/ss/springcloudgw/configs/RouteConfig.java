package com.ss.springcloudgw.configs;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "route")
@Data
public class RouteConfig {

    private String routeApi;

}
