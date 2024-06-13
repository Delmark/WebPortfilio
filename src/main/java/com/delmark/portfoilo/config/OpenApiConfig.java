package com.delmark.portfoilo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI myOpenAPI() {

        // TODO: Remove hardcore server URL
        Server server = new Server().
                url("localhost:8080").
                description("Main server");

        Info info = new Info()
                .title("DelmPortfolio API")
                .version("1.0")
                .description("This API is used to manage the portfolio of a developer");
        return new OpenAPI().info(info).servers(List.of(server));
    }
}
