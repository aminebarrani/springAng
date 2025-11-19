package com.example.stageproj.entity;

import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String role = "USER";

    @Column(name = "phoneNumber")
    private String phoneNumber = "";

    @Column(name = "firstname")
    private String firstname = "";

    @Column(name = "lastname")
    private String lastname = "";

    @Lob
    @Column(name = "image", columnDefinition = "LONGTEXT")
    private String image = "";

    private boolean enabled = true;

    // New relationship with Personne
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "personne_id")
    private Personne personne;

    // Constructors
    public User() {}

    public User(String username, String password, String email, String image) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.image = image;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    @Override
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    @Override
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getFirstname() { return firstname; }
    public void setFirstname(String firstname) { this.firstname = firstname; }

    public String getLastname() { return lastname; }
    public void setLastname(String lastname) { this.lastname = lastname; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public Personne getPersonne() { return personne; }
    public void setPersonne(Personne personne) { this.personne = personne; }

    // UserDetails implementation
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role));
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return enabled; }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='[PROTECTED]'" +
                ", email='" + email + '\'' +
                ", role='" + role + '\'' +
                ", phoneNumber='" + phoneNumber + '\'' +
                ", firstname='" + firstname + '\'' +
                ", lastname='" + lastname + '\'' +
                ", image='" + (image != null ? (image.length() > 30 ? image.substring(0, 30) + "..." : image) : null) + '\'' +
                ", enabled=" + enabled +
                ", personne=" + (personne != null ? personne.getIdPersonne() : null) +
                '}';
    }

    @PrePersist
    public void prePersist() {
        if (phoneNumber == null) phoneNumber = "";
        if (firstname == null) firstname = "";
        if (lastname == null) lastname = "";
        if (role == null) role = "USER";
        if (image == null) image = "";
    }
}