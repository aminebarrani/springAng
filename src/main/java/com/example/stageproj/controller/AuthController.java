package com.example.stageproj.controller;

import com.example.stageproj.dto.AuthResponse;
import com.example.stageproj.dto.LoginRequest;
import com.example.stageproj.dto.RegisterRequest;
import com.example.stageproj.dto.UpdateProfileRequest;
import com.example.stageproj.entity.User;
import com.example.stageproj.entity.Personne;
import com.example.stageproj.repository.UserRepository;
import com.example.stageproj.repository.PersonneRepository;
import com.example.stageproj.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired private AuthenticationManager authenticationManager;
    @Autowired private UserRepository userRepository;
    @Autowired private PersonneRepository personneRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

            User user = userRepository.findByUsername(loginRequest.getUsername())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            String token = jwtService.generateToken(user);
            return ResponseEntity.ok(new AuthResponse(token, user));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new AuthResponse("Invalid credentials"));
        }
    }

    @GetMapping("/user")
    public ResponseEntity<?> getUserProfile(Authentication authentication) {
        try {
            User user = userRepository.findByUsername(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            return ResponseEntity.ok(new AuthResponse("", user));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error fetching user profile");
        }
    }

    @PutMapping("/user")
    public ResponseEntity<?> updateUserProfile(@RequestBody UpdateProfileRequest updateRequest, Authentication authentication) {
        try {
            User user = userRepository.findByUsername(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (updateRequest.getEmail() != null) user.setEmail(updateRequest.getEmail());
            if (updateRequest.getPhoneNumber() != null) user.setPhoneNumber(updateRequest.getPhoneNumber());
            if (updateRequest.getFirstname() != null) user.setFirstname(updateRequest.getFirstname());
            if (updateRequest.getLastname() != null) user.setLastname(updateRequest.getLastname());
            if (updateRequest.getImage() != null) user.setImage(updateRequest.getImage());
            if (updateRequest.getPassword() != null && !updateRequest.getPassword().isEmpty()) {
                user.setPassword(passwordEncoder.encode(updateRequest.getPassword()));
            }
            User updatedUser = userRepository.save(user);
            return ResponseEntity.ok(new AuthResponse("", updatedUser));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error updating user profile");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) {
        try {
            System.out.println("DEBUG: Received image from frontend: " + (registerRequest.getImage() != null ? (registerRequest.getImage().length() > 30 ? registerRequest.getImage().substring(0, 30) + "..." : registerRequest.getImage()) : null));
            if (userRepository.existsByUsername(registerRequest.getUsername())) {
                return ResponseEntity.badRequest().body(new AuthResponse("Username exists"));
            }
            if (userRepository.existsByEmail(registerRequest.getEmail())) {
                return ResponseEntity.badRequest().body(new AuthResponse("Email exists"));
            }

            User user = new User();
            user.setUsername(registerRequest.getUsername());
            user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
            user.setEmail(registerRequest.getEmail());
            user.setPhoneNumber(registerRequest.getPhoneNumber() != null ? registerRequest.getPhoneNumber() : "");
            user.setFirstname(registerRequest.getFirstname() != null ? registerRequest.getFirstname() : "");
            user.setLastname(registerRequest.getLastname() != null ? registerRequest.getLastname() : "");
            user.setRole(registerRequest.getRole() != null ? registerRequest.getRole() : "USER");
            user.setImage(registerRequest.getImage() != null ? registerRequest.getImage() : "");

            // Link user to personne if personneId is provided
            if (registerRequest.getPersonneId() != null) {
                Optional<Personne> personne = personneRepository.findById(registerRequest.getPersonneId());
                if (personne.isPresent()) {
                    user.setPersonne(personne.get());
                    // If role is PERSONNE, ensure it's set correctly
                    if ("PERSONNE".equals(registerRequest.getRole())) {
                        user.setRole("PERSONNE");
                    }
                } else {
                    return ResponseEntity.badRequest().body(new AuthResponse("Personne not found"));
                }
            }

            System.out.println("DEBUG: User entity before save: " + user);

            User savedUser = userRepository.save(user);
            System.out.println("DEBUG: Saved user image in DB: " + (savedUser.getImage() != null ? (savedUser.getImage().length() > 30 ? savedUser.getImage().substring(0, 30) + "..." : savedUser.getImage()) : null));
            String token = jwtService.generateToken(savedUser);
            return ResponseEntity.ok(new AuthResponse(token, savedUser));

        } catch (Exception e) {
            return ResponseEntity.status(500).body(new AuthResponse("Registration failed"));
        }
    }
}