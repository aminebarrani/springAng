package com.example.stageproj.config;

import com.example.stageproj.entity.Department;
import com.example.stageproj.entity.Personne;
import com.example.stageproj.entity.User;
import com.example.stageproj.repository.DepartmentRepository;
import com.example.stageproj.repository.PersonneRepository;
import com.example.stageproj.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private PersonneRepository personneRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Load departments
        if (departmentRepository.count() == 0) {
            Department dept1 = new Department();
            dept1.setNomDept("IT");
            dept1.setDescription("Information Technology");
            departmentRepository.save(dept1);

            Department dept2 = new Department();
            dept2.setNomDept("HR");
            dept2.setDescription("Human Resources");
            departmentRepository.save(dept2);

            Department dept3 = new Department();
            dept3.setNomDept("Finance");
            dept3.setDescription("Finance Department");
            departmentRepository.save(dept3);
        }

        // Load personnes
        if (personneRepository.count() == 0) {
            Department itDept = departmentRepository.findByNomDept("IT").orElse(null);
            Department hrDept = departmentRepository.findByNomDept("HR").orElse(null);

            if (itDept != null) {
                Personne personne1 = new Personne();
                personne1.setNom("Doe");
                personne1.setPrenom("John");
                personne1.setAdresse("123 Main St");
                personne1.setDepartment(itDept);
                personneRepository.save(personne1);

                Personne personne2 = new Personne();
                personne2.setNom("Smith");
                personne2.setPrenom("Jane");
                personne2.setAdresse("456 Oak Ave");
                personne2.setDepartment(itDept);
                personneRepository.save(personne2);
            }

            if (hrDept != null) {
                Personne personne3 = new Personne();
                personne3.setNom("Johnson");
                personne3.setPrenom("Bob");
                personne3.setAdresse("789 Pine Rd");
                personne3.setDepartment(hrDept);
                personneRepository.save(personne3);
            }
        }

        // Load users for authentication
        if (userRepository.count() == 0) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setEmail("admin@example.com");
            admin.setRole("ADMIN");
            userRepository.save(admin);

            User user = new User();
            user.setUsername("user");
            user.setPassword(passwordEncoder.encode("user123"));
            user.setEmail("user@example.com");
            user.setRole("USER");
            userRepository.save(user);
        }

        System.out.println("Data loaded successfully!");
    }
} 