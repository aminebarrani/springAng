package com.example.stageproj.dto;

public class RegisterRequest {
    private String username;
    private String password;
    private String email;
    private String role;
    private String phoneNumber;
    private String firstname;
    private String lastname;
    private String image;
    private Long personneId;

    // Constructors
    public RegisterRequest() {}

    public RegisterRequest(String username, String password, String email, String role) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
    }

    public RegisterRequest(String username, String password, String email, String role, String phoneNumber, String firstname, String lastname, String image) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
        this.phoneNumber = phoneNumber;
        this.firstname = firstname;
        this.lastname = lastname;
        this.image = image;
    }

    public RegisterRequest(String username, String password, String email, String role, String phoneNumber, String firstname, String lastname, String image, Long personneId) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
        this.phoneNumber = phoneNumber;
        this.firstname = firstname;
        this.lastname = lastname;
        this.image = image;
        this.personneId = personneId;
    }

    // Getters and Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public Long getPersonneId() { return personneId; }
    public void setPersonneId(Long personneId) { this.personneId = personneId; }
} 