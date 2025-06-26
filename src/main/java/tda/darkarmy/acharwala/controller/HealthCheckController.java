package tda.darkarmy.acharwala.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.ResponseEntity.status;

@RestController
@RequestMapping("/health-check")
public class HealthCheckController {

    @GetMapping
    public ResponseEntity<?> checkHealth(){
        return status(200).body("Server is up and running cool!");
    }
}
