package com.example.stageproj.controller;

import com.example.stageproj.entity.Personne;
import com.example.stageproj.entity.Department;
import com.example.stageproj.repository.PersonneRepository;
import com.example.stageproj.repository.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@RestController
@RequestMapping("/api/personnes")
public class PersonneController {

    @Autowired
    private PersonneRepository personneRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    // Get all personnes with their toDoLists
    @GetMapping
    public List<Personne> getAllPersonnes() {
        return personneRepository.findAllWithToDoLists();
    }

    // Get personne by ID with toDoLists
    @GetMapping("/{id}")
    public ResponseEntity<Personne> getPersonneById(@PathVariable Long id) {
        System.out.println("DEBUG: Attempting to find personne with ID: " + id);
        try {
            Optional<Personne> personne = personneRepository.findByIdWithToDoLists(id);
            if (personne.isPresent()) {
                System.out.println("DEBUG: Found personne: " + personne.get().getNom() + " " + personne.get().getPrenom());
                return ResponseEntity.ok(personne.get());
            } else {
                System.out.println("DEBUG: No personne found with ID: " + id);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            System.err.println("DEBUG: Error finding personne with ID " + id + ": " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    // Get all personnes by department ID
    @GetMapping("/department/{idDept}")
    public ResponseEntity<List<Personne>> getPersonnesByDepartment(@PathVariable Long idDept) {
        List<Personne> personnes = personneRepository.findByDepartmentIdDept(idDept);
        return ResponseEntity.ok(personnes);
    }

    // Create new personne
    @PostMapping
    public ResponseEntity<?> createPersonne(@RequestBody Personne personne) {
        if (personne.getDepartment() == null || personne.getDepartment().getIdDept() == null) {
            return ResponseEntity.badRequest().body("Department ID is required");
        }
        Optional<Department> dept = departmentRepository.findById(personne.getDepartment().getIdDept());
        if (dept.isEmpty()) {
            return ResponseEntity.badRequest().body("Department not found");
        }
        personne.setDepartment(dept.get());
        Personne saved = personneRepository.save(personne);
        return ResponseEntity.ok(saved);
    }

    // Update personne
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePersonne(@PathVariable Long id, @RequestBody Personne personneDetails) {
        Optional<Personne> personneOpt = personneRepository.findById(id);
        if (personneOpt.isPresent()) {
            Personne personne = personneOpt.get();
            personne.setNom(personneDetails.getNom());
            personne.setPrenom(personneDetails.getPrenom());
            personne.setAdresse(personneDetails.getAdresse());
            if (personneDetails.getDepartment() != null && personneDetails.getDepartment().getIdDept() != null) {
                Optional<Department> dept = departmentRepository.findById(personneDetails.getDepartment().getIdDept());
                if (dept.isEmpty()) {
                    return ResponseEntity.badRequest().body("Department not found");
                }
                personne.setDepartment(dept.get());
            }
            return ResponseEntity.ok(personneRepository.save(personne));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Delete personne
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePersonne(@PathVariable Long id) {
        Optional<Personne> personne = personneRepository.findById(id);
        if (personne.isPresent()) {
            personneRepository.deleteById(id);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Test endpoint to check if database is connected for Personne
    @GetMapping("/test")
    public ResponseEntity<String> testConnection() {
        try {
            long count = personneRepository.count();
            return ResponseEntity.ok("Database connection successful! Total personnes: " + count);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Database connection failed: " + e.getMessage());
        }
    }

    // Test endpoint using standard findById (without toDoLists)
    @GetMapping("/test/{id}")
    public ResponseEntity<?> testFindById(@PathVariable Long id) {
        System.out.println("DEBUG: Testing standard findById for ID: " + id);
        try {
            Optional<Personne> personne = personneRepository.findById(id);
            if (personne.isPresent()) {
                System.out.println("DEBUG: Standard findById found personne: " + personne.get().getNom() + " " + personne.get().getPrenom());
                return ResponseEntity.ok(personne.get());
            } else {
                System.out.println("DEBUG: Standard findById found no personne with ID: " + id);
                return ResponseEntity.status(404).body("Personne not found with ID: " + id);
            }
        } catch (Exception e) {
            System.err.println("DEBUG: Error in standard findById for ID " + id + ": " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
} 