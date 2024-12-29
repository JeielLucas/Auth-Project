package com.auth.api.configuration;


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
                .title("Auth api")
                .version("1.0")
                .description("Essa api mostra as rotas para login, registro, reset de senha, ativação do usuário e validação do jwt")
                .contact(myContact);


        return new OpenAPI().info(information).servers(List.of(server));
    }
}
