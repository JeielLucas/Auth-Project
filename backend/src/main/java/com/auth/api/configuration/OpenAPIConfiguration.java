package com.auth.api.configuration;

import com.auth.api.dtos.ApiResponses;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenAPIConfiguration {

    @Bean
    public OpenAPI defineOpenAPI() {

        Server server = new Server();
        server.setUrl("http://localhost:8080");
        server.setDescription("Development");

        Contact myContact = new Contact();
        myContact.setName("Jeiel Lucas");
        myContact.setEmail("jeiellucas0404@gmail.com");

        Info information = new Info()
                .title("Auth API")
                .version("1.0")
                .description("Essa API mostra as rotas para login, registro, reset de senha, ativação do usuário e validação do JWT")
                .contact(myContact);
        return new OpenAPI().info(information).servers(List.of(server))
                .components(new Components()
                        .addResponses("successful_create", ApiResponses.SUCCESSFUL_CREATE)
                        .addResponses("invalid_credentials", ApiResponses.INVALID_CREDENTIALS)
                        .addResponses("email_already_exists", ApiResponses.EMAIL_ALREADY_EXISTS)
                        .addResponses("unauthorized", ApiResponses.UNAUTHORIZED)
                        .addResponses("successful_login", ApiResponses.SUCCESSFUL_LOGIN)
                        .addResponses("account_not_activated", ApiResponses.ACCOUNT_NOT_ACTIVATED)
                        .addResponses("ping", ApiResponses.PING)
                        .addResponses("valid_token", ApiResponses.VALID_TOKEN)
                        .addResponses("not_valid_token", ApiResponses.NOT_VALID_TOKEN)
                        .addResponses("activate", ApiResponses.ACTIVATE_ACCOUNT)
                        .addResponses("invalid_token", ApiResponses.INVALID_TOKEN)
                        .addResponses("invalid_operation", ApiResponses.INVALID_OPERATION)
                        .addResponses("google_login", ApiResponses.REDIRECT_GOOGLE_LOGIN)
                        .addResponses("invalid_google_token", ApiResponses.INVALID_GOOGLE_TOKEN)
                        .addResponses("reset_password", ApiResponses.RESET_PASSWORD)
                        .addResponses("reuse_password", ApiResponses.REUSE_PASSWORD)
                        .addResponses("successful_reset_password", ApiResponses.SUCCESSFUL_RESET_PASSWORD)
                );
    }
}
