package com.petadoption.backend.services;

import com.petadoption.backend.model.User;
import com.petadoption.backend.model.UserDTO;
import com.petadoption.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public User registerUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public Optional<User> loginUser(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent() && passwordEncoder.matches(password, userOpt.get().getPassword())) {
            return userOpt;
        }
        return Optional.empty();
    }

    public Optional<User> getUserById(String id) {
        return userRepository.findById(id);
    }

    public User saveUser(User user) {
        // Only encode password if it's not already encoded (simple check)
        if (user.getPassword() != null && !user.getPassword().startsWith("$2a$")) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return userRepository.save(user);
    }

    public Optional<UserDTO> findByEmailAndPassword(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            try {
                // Debug: Print user data to understand its structure
                System.out.println("User found: " + user.getEmail());
                System.out.println("User ID: " + user.getId());
                System.out.println("User name: " + user.getName());
                System.out.println("User role: " + user.getRole());

                if (passwordEncoder.matches(password, user.getPassword())) {
                    return Optional.of(new UserDTO(user.getId(), user.getName(), user.getEmail(), user.getRole()));
                } else {
                    System.out.println("Password does not match for user: " + email);
                }
            } catch (Exception e) {
                // Log the error for debugging
                System.err.println("Error processing user data: " + e.getMessage());
                e.printStackTrace();
                return Optional.empty();
            }
        } else {
            System.out.println("No user found for email: " + email);
        }
        return Optional.empty();
    }

    public List<User> getAllUsers() {
        System.out.println("UserService - getAllUsers called");
        List<User> users = userRepository.findAll();
        System.out.println("UserService - Found " + users.size() + " users");
        return users;
    }

    public List<UserDTO> getAllUsersForChat() {
        System.out.println("UserService - getAllUsersForChat called");
        List<User> users = userRepository.findAll();
        List<UserDTO> userDTOs = users.stream()
            .map(user -> new UserDTO(user.getId(), user.getName(), user.getEmail(), user.getRole()))
            .collect(Collectors.toList());
        System.out.println("UserService - Found " + userDTOs.size() + " users for chat");
        return userDTOs;
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public String resetPassword(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent()) {
            // Generate a temporary password (in a real app, you'd send this via email)
            String tempPassword = generateTempPassword();
            User user = userOpt.get();
            user.setPassword(passwordEncoder.encode(tempPassword));
            userRepository.save(user);
            return tempPassword;
        }
        return null;
    }

    public void deleteUser(String id) {
        userRepository.deleteById(id);
    }

    private String generateTempPassword() {
        // Simple temporary password generation
        return "TempPass" + (int)(Math.random() * 10000);
    }
}
