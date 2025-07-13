package com.example.stageproj.service;

import com.example.stageproj.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("UserDetailsService - Loading user by username: " + username);
        
        return userRepository.findByUsername(username)
                .map(user -> {
                    System.out.println("UserDetailsService - Found user: " + user.getUsername() + " with role: " + user.getRole());
                    System.out.println("UserDetailsService - User authorities: " + user.getAuthorities());
                    return user;
                })
                .orElseThrow(() -> {
                    System.out.println("UserDetailsService - User not found: " + username);
                    return new UsernameNotFoundException("User not found with username: " + username);
                });
    }
} 