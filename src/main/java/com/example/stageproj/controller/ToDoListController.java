package com.example.stageproj.controller;

import com.example.stageproj.entity.ToDoList;
import com.example.stageproj.entity.Personne;
import com.example.stageproj.repository.ToDoListRepository;
import com.example.stageproj.repository.PersonneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/todolist")
public class ToDoListController {

    @Autowired
    private ToDoListRepository toDoListRepository;

    @Autowired
    private PersonneRepository personneRepository;

    // Get all tasks
    @GetMapping
    public List<ToDoList> getAllTasks() {
        return toDoListRepository.findAll();
    }

    // Get task by ID
    @GetMapping("/{id}")
    public ResponseEntity<ToDoList> getTaskById(@PathVariable Long id) {
        Optional<ToDoList> task = toDoListRepository.findById(id);
        return task.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Get all tasks by personne ID
    @GetMapping("/personne/{personneId}")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<ToDoList>> getTasksByPersonne(@PathVariable Long personneId) {
        List<ToDoList> tasks = toDoListRepository.findByPersonneIdPersonne(personneId);
        System.out.println("fetch todo lists");
        System.out.println(tasks);
        return ResponseEntity.ok(tasks);
    }

    // Create new task
    @PostMapping
    public ResponseEntity<?> createTask(@RequestBody ToDoList toDoList) {
        if (toDoList.getPersonne() == null || toDoList.getPersonne().getIdPersonne() == null) {
            return ResponseEntity.badRequest().body("Personne ID is required");
        }
        Optional<Personne> personne = personneRepository.findById(toDoList.getPersonne().getIdPersonne());
        if (personne.isEmpty()) {
            return ResponseEntity.badRequest().body("Personne not found");
        }
        toDoList.setPersonne(personne.get());

        // Set default value for isChecked if not provided
        if (toDoList.getIsChecked() == null) {
            toDoList.setIsChecked(false);
        }

        ToDoList saved = toDoListRepository.save(toDoList);
        return ResponseEntity.ok(saved);
    }

    // Update task
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTask(@PathVariable Long id, @RequestBody ToDoList toDoListDetails) {
        Optional<ToDoList> taskOpt = toDoListRepository.findById(id);
        if (taskOpt.isPresent()) {
            ToDoList task = taskOpt.get();
            task.setDescrip(toDoListDetails.getDescrip());

            if (toDoListDetails.getPersonne() != null && toDoListDetails.getPersonne().getIdPersonne() != null) {
                Optional<Personne> personne = personneRepository.findById(toDoListDetails.getPersonne().getIdPersonne());
                if (personne.isEmpty()) {
                    return ResponseEntity.badRequest().body("Personne not found");
                }
                task.setPersonne(personne.get());
            }

            // Update all fields
            task.setDatebeb(toDoListDetails.getDatebeb());
            task.setDatefin(toDoListDetails.getDatefin());
            task.setIsChecked(toDoListDetails.getIsChecked());

            return ResponseEntity.ok(toDoListRepository.save(task));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Delete task
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable Long id) {
        Optional<ToDoList> task = toDoListRepository.findById(id);
        if (task.isPresent()) {
            toDoListRepository.deleteById(id);
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Test endpoint to check if database is connected for ToDoList
    @GetMapping("/test")
    public ResponseEntity<String> testConnection() {
        try {
            long count = toDoListRepository.count();
            return ResponseEntity.ok("Database connection successful! Total tasks: " + count);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Database connection failed: " + e.getMessage());
        }
    }
}