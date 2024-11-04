package comp.security.Server;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Server {
    @SuppressWarnings("rawtypes")
    @GetMapping("/")
    public ResponseEntity get(){
        return ResponseEntity.ok("Hello World");
    }
}
