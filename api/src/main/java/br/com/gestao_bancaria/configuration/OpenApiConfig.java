package br.com.gestao_bancaria.configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("API - Sistema de Gestão Bancária")
                        .description("""
                            API para gerenciamento de contas bancárias, clientes e movimentações financeiras.
                            Permite operações de depósito e saque com validações de saldo e CPF, além de controle
                            de dados vinculados a cada pessoa cadastrada.
                            """)
                        .version("v1")
                        .termsOfService("https://github.com/SENAI-SD/prova-java-junior-00933-2025-027.007.911-42"));
    }
}
