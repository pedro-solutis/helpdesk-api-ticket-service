package br.com.solutis.helpdesk.api_ticket_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients 
public class ApiTicketServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiTicketServiceApplication.class, args);
	}

}
