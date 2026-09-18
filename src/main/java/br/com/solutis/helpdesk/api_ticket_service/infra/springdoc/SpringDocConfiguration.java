package br.com.solutis.helpdesk.api_ticket_service.infra.springdoc;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

@Configuration
public class SpringDocConfiguration {

    @Bean
    public OpenAPI customOpenAPI(){
        return new OpenAPI()
            .info(new Info()
                .title("solutis.helpdesk.ticket API")
                .description("Ticket Service API Rest with CRUD, filters and search functionalities.")
                .contact(new Contact()
                    .name("Pedro Ferreira")
                    .email("pedro.ferreira@solutis.com.br")
            )
            .license(new License()
                .name("MIT")
            )
        );
    }

}
