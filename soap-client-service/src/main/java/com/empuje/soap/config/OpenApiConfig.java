package com.empuje.soap.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("SOAP Client Service API")
                        .version("1.0.0")
                        .description("Cliente SOAP para consultar presidentes y ONGs de la red")
                        .contact(new Contact()
                                .name("Equipo Empuje Comunitario")
                                .email("admin@empuje.org")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8087")
                                .description("Servidor de desarrollo")));
    }
}
