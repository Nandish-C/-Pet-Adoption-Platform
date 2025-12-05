package com.petadoption.backend.controller;

import com.petadoption.backend.config.JwtUtil;
import com.petadoption.backend.model.User;
import com.petadoption.backend.model.UserDTO;
import com.petadoption.backend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:4200") // Adjust to your Angular port
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        // Check if email already exists
        if (userService.findByEmail(user.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email already exists");
        }
        User savedUser = userService.saveUser(user);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Registration successful. Please login.");
        response.put("userId", savedUser.getId());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody User user) {
        Optional<UserDTO> existingUser = userService.findByEmailAndPassword(user.getEmail(), user.getPassword());
        if (existingUser.isPresent()) {
            UserDTO loggedInUser = existingUser.get();
            System.out.println("UserController - Login - UserDTO email: " + loggedInUser.getEmail());
            System.out.println("UserController - Login - UserDTO id: " + loggedInUser.getId());
            String token = jwtUtil.generateToken(loggedInUser.getEmail());
            System.out.println("UserController - Login - Generated token for email: " + loggedInUser.getEmail());

            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("user", loggedInUser);

            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(401).body("Invalid credentials");
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        if (email == null || email.isEmpty()) {
            return ResponseEntity.badRequest().body("Email is required");
        }

        String tempPassword = userService.resetPassword(email);
        if (tempPassword != null) {
            Map<String, String> response = new HashMap<>();
            response.put("message", "Password reset successful. Your temporary password is: " + tempPassword);
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(404).body("User not found with email: " + email);
        }
    }

    @GetMapping("/users-for-chat")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<List<UserDTO>> getUsersForChat() {
        List<UserDTO> users = userService.getAllUsersForChat();
        return ResponseEntity.ok(users);
    }
}
