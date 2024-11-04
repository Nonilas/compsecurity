// Client.java - Updated to include UUID generation, registration logic, and actions
package comp.security.Client;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

@Component
public class Client {
    private final String registerUrl = "http://localhost:8080/register";
    private final String actionUrl = "http://localhost:8080/action/";
    private UUID clientId;
    private String password;

    public Client() {
        // Generate a unique UUID for the client and set a password
        this.clientId = UUID.randomUUID();
        this.password = "my_secure_password";
    }

    public void startClient() {
        RestTemplate restTemplate = new RestTemplate();
        ClientRegistrationRequest request = new ClientRegistrationRequest(clientId.toString(), password);

        // Register the client at the server
        ResponseEntity<String> response = restTemplate.postForEntity(registerUrl, request, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            System.out.println("Server Response: " + response.getBody());
            startActions();
        } else {
            System.err.println("Failed to register with server. Status code: " + response.getStatusCode());
        }
    }

    private void startActions() {
        String[] actions = {"INCREASE", "DECREASE", "INCREASE"};
        int[] amounts = {10, 5, 15};
        int delay = 2000; // Delay in milliseconds between actions

        Timer timer = new Timer();
        for (int i = 0; i < actions.length; i++) {
            String action = actions[i];
            int amount = amounts[i];

            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    performAction(action, amount);
                }
            }, delay * i);
        }
    }

    private void performAction(String action, int amount) {
        RestTemplate restTemplate = new RestTemplate();
        ClientActionRequest actionRequest = new ClientActionRequest(password, action, amount);
        ResponseEntity<String> response = restTemplate.postForEntity(actionUrl + clientId, actionRequest, String.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            System.out.println("Action performed: " + action + " " + amount);
            System.out.println("Server Response: " + response.getBody());} else {
                System.err.println("Failed to perform action. Status code: " + response.getStatusCode());
            }
        }
    }
    
class ClientRegistrationRequest {
    private String id;
    private String password;

    public ClientRegistrationRequest(String id, String password) {
        this.id = id;
        this.password = password;
    }

    public String getId() {
        return id;
    }

    public String getPassword() {
        return password;
    }
}
class ClientActionRequest {
    private String password;
    private String action;
    private int amount;

    public ClientActionRequest(String password, String action, int amount) {
        this.password = password;
        this.action = action;
        this.amount = amount;
    }

    public String getPassword() {
        return password;
    }

    public String getAction() {
        return action;
    }

    public int getAmount() {
        return amount;
    }
}