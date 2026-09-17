package com.biblioteca.auth_service.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI authOpenAPI() {
        Server server = new Server();
        server.setUrl("/");

        return new OpenAPI()
                .servers(List.of(server))
                .info(new Info()
                        .title("Auth Service API")
                        .description("Registro de usuarios y autenticación con JWT (roles ADMIN, BIBLIOTECARIO y CLIENTE)")
                        .version("1.0"));
    }
}
