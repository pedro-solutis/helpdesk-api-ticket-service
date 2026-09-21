package br.com.solutis.helpdesk.api_ticket_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;

@OpenAPIDefinition(servers = { @Server(url = "http://localhost:8080/api") })
@SpringBootApplication
@EnableFeignClients 
public class ApiTicketServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiTicketServiceApplication.class, args);
	}

}
