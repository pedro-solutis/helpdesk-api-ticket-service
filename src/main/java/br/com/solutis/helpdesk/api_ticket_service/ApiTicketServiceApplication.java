package br.com.solutis.helpdesk.api_ticket_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(servers = { @Server(url = "${GATEWAY_URL:http://localhost:8080/api}", description = "Gateway API"), @Server(url = "${LOCAL_URL:http://localhost:8082}", description = "Local Server") })
@SpringBootApplication
@EnableFeignClients 
public class ApiTicketServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiTicketServiceApplication.class, args);
	}

}
