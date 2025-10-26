package com.empuje.filters.config;

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
                        .title("Report Filters Service API")
                        .version("1.0.0")
                        .description("API para gestión de filtros personalizados y exportación de informes")
                        .contact(new Contact()
                                .name("Equipo Empuje Comunitario")
                                .email("admin@empuje.org")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8086")
                                .description("Servidor de desarrollo")));
    }
}
