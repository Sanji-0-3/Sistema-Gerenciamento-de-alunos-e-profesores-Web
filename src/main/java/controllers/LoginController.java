package controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/login")
@CrossOrigin(origins = "*") // Permite a comunicação com o frontend
public class LoginController {

    public static class LoginRequest {
        public String username;
        public String password;
    }
    
    public static class LoginResponse {
        public boolean success;
        public String message;
    }

    @PostMapping
    public ResponseEntity<LoginResponse> fazerLogin(@RequestBody LoginRequest request) {
        LoginResponse response = new LoginResponse();
        
        // Credenciais fixas (replica a lógica de LoginFrame.java e script.js)
        if ("admin".equals(request.username) && "1234".equals(request.password)) {
            response.success = true;
            response.message = "Login bem-sucedido!";
            return ResponseEntity.ok(response);
        } else {
            response.success = false;
            response.message = "Usuário ou senha incorretos.";
            return ResponseEntity.status(401).body(response);
        }
    }
}
