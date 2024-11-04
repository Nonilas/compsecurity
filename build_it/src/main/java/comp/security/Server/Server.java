// Server.java - Updated to handle client registration with unique UUID and actions
package comp.security.Server;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import comp.security.Server.ClientActionRequest;
import comp.security.Server.ClientRegistrationRequest;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

@RestController
public class Server {
    // Store registered clients with UUID as key and password as value, along with counters
    private final Map<UUID, ClientData> registeredClients = new ConcurrentHashMap<>();
    private final Logger logger = Logger.getLogger(Server.class.getName());

    public Server() {
        try {
            FileHandler fileHandler = new FileHandler("logfile.txt", true);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Register a new client with a unique UUID and password
    @PostMapping("/register")public ResponseEntity<String> registerClient(@RequestBody ClientRegistrationRequest request) {
        UUID clientId = UUID.fromString(request.getId());
        String password = request.getPassword();

        if (registeredClients.containsKey(clientId)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Client with the same ID is already registered.");
        }

        registeredClients.put(clientId, new ClientData(password));
        return ResponseEntity.ok("Client registered successfully with ID: " + clientId);
    }

    // Perform an action on the client's counter (INCREASE/DECREASE)
    @PostMapping("/action/{clientId}")
    public ResponseEntity<String> performAction(@PathVariable UUID clientId, @RequestBody ClientActionRequest actionRequest) {
        if (!registeredClients.containsKey(clientId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Client not found.");
        }

        ClientData clientData = registeredClients.get(clientId);
        if (!clientData.getPassword().equals(actionRequest.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid password.");
        }

        int amount = actionRequest.getAmount();
        String action = actionRequest.getAction();

        if (action.equalsIgnoreCase("INCREASE")) {
            clientData.getCounter().addAndGet(amount);
        } else if (action.equalsIgnoreCase("DECREASE")) {
            clientData.getCounter().addAndGet(-amount);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid action.");
        }
        int newValue = clientData.getCounter().get();
        logger.info("Client " + clientId + ": Counter updated to " + newValue);
        return ResponseEntity.ok("Counter updated to " + newValue);
    }

    // Check if a client is registered
    @GetMapping("/check/{clientId}")
    public ResponseEntity<String> checkClient(@PathVariable UUID clientId) {
        if (registeredClients.containsKey(clientId)) {
            return ResponseEntity.ok("Client is registered.");
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Client not found.");
    }

    // Inner class to store client data
    static class ClientData {
        private final String password;
        private final AtomicInteger counter;

        public ClientData(String password) {
            this.password = password;
            this.counter = new AtomicInteger(0);
        }

        public String getPassword() {
            return password;
        }

        public AtomicInteger getCounter() {
            return counter;
        }
    }
}
