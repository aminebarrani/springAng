package com.example.stageproj.dto;

import com.example.stageproj.entity.User;

public class AuthResponse {
    private String token;
    private String username;
    private String role;
    private String email;
    private String firstname;
    private String lastname;
    private String phoneNumber;
    private String image;
    private String message;

    public AuthResponse() {}

    public AuthResponse(String token, User user) {
        this.token = token;
        this.username = user.getUsername();
        this.role = user.getRole();
        this.email = user.getEmail();
        this.firstname = user.getFirstname();
        this.lastname = user.getLastname();
        this.phoneNumber = user.getPhoneNumber();
        this.image = user.getImage();
    }

    public AuthResponse(String message) {
        this.message = message;
    }

    // Getters and Setters
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getFirstname() { return firstname; }
    public void setFirstname(String firstname) { this.firstname = firstname; }
    public String getLastname() { return lastname; }
    public void setLastname(String lastname) { this.lastname = lastname; }
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}