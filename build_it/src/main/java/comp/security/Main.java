
// Main.java - Updated to start both server and client
package comp.security;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import comp.security.Client.Client;

@SpringBootApplication
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
        return args -> {
            // Start the server (already running with Spring Boot context)
            System.out.println("Server is up and running!");

            // Create and start the client
            Client client = ctx.getBean(Client.class);
            client.startClient();
        };
    }
}
