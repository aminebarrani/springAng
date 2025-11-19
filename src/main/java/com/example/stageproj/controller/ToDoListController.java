package com.example.stageproj.controller;

import com.example.stageproj.entity.ToDoList;
import com.example.stageproj.entity.Personne;
import com.example.stageproj.entity.User;
import com.example.stageproj.repository.ToDoListRepository;
import com.example.stageproj.repository.PersonneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    // Get all tasks (Admin only)
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<ToDoList> getAllTasks() {
        return toDoListRepository.findAll();
    }

    // Get task by ID (Admin or owner)
    @GetMapping("/{id}")
    public ResponseEntity<ToDoList> getTaskById(@PathVariable Long id) {
        Optional<ToDoList> task = toDoListRepository.findById(id);
        if (task.isPresent()) {
            // Check if user is admin or the owner of the task
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User currentUser = (User) auth.getPrincipal();
            
            if (currentUser.getRole().equals("ADMIN") || 
                (currentUser.getPersonne() != null && 
                 currentUser.getPersonne().getIdPersonne().equals(task.get().getPersonne().getIdPersonne()))) {
                return ResponseEntity.ok(task.get());
            } else {
                return ResponseEntity.status(403).build();
            }
        }
        return ResponseEntity.notFound().build();
    }

    // Get current user's todos (for PERSONNE role)
    @GetMapping("/my-todos")
    @PreAuthorize("hasAnyRole('PERSONNE', 'ADMIN')")
    public ResponseEntity<List<ToDoList>> getMyTodos() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();
        
        if (currentUser.getPersonne() == null) {
            return ResponseEntity.badRequest().build();
        }
        
        List<ToDoList> tasks = toDoListRepository.findByPersonneIdPersonne(currentUser.getPersonne().getIdPersonne());
        return ResponseEntity.ok(tasks);
    }

    // Get all tasks by personne ID (Admin only)
    @GetMapping("/personne/{personneId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ToDoList>> getTasksByPersonne(@PathVariable Long personneId) {
        List<ToDoList> tasks = toDoListRepository.findByPersonneIdPersonne(personneId);
        System.out.println("fetch todo lists");
        System.out.println(tasks);
        return ResponseEntity.ok(tasks);
    }

    // Create new task (Admin or PERSONNE for their own tasks)
    @PostMapping
    public ResponseEntity<?> createTask(@RequestBody ToDoList toDoList) {
        if (toDoList.getPersonne() == null || toDoList.getPersonne().getIdPersonne() == null) {
            return ResponseEntity.badRequest().body("Personne ID is required");
        }
        
        // Check if user can create task for this personne
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = (User) auth.getPrincipal();
        
        if (!currentUser.getRole().equals("ADMIN") && 
            (currentUser.getPersonne() == null || 
             !currentUser.getPersonne().getIdPersonne().equals(toDoList.getPersonne().getIdPersonne()))) {
            return ResponseEntity.status(403).body("You can only create tasks for yourself");
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

    // Update task (Admin or owner)
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTask(@PathVariable Long id, @RequestBody ToDoList toDoListDetails) {
        Optional<ToDoList> taskOpt = toDoListRepository.findById(id);
        if (taskOpt.isPresent()) {
            ToDoList task = taskOpt.get();
            
            // Check if user can update this task
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User currentUser = (User) auth.getPrincipal();
            
            if (!currentUser.getRole().equals("ADMIN") && 
                (currentUser.getPersonne() == null || 
                 !currentUser.getPersonne().getIdPersonne().equals(task.getPersonne().getIdPersonne()))) {
                return ResponseEntity.status(403).body("You can only update your own tasks");
            }
            
            task.setDescrip(toDoListDetails.getDescrip());

            if (toDoListDetails.getPersonne() != null && toDoListDetails.getPersonne().getIdPersonne() != null) {
                // Only admin can change task ownership
                if (!currentUser.getRole().equals("ADMIN")) {
                    return ResponseEntity.status(403).body("Only admins can change task ownership");
                }
                
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

    // Delete task (Admin or owner)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTask(@PathVariable Long id) {
        Optional<ToDoList> task = toDoListRepository.findById(id);
        if (task.isPresent()) {
            // Check if user can delete this task
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User currentUser = (User) auth.getPrincipal();
            
            if (!currentUser.getRole().equals("ADMIN") && 
                (currentUser.getPersonne() == null || 
                 !currentUser.getPersonne().getIdPersonne().equals(task.get().getPersonne().getIdPersonne()))) {
                return ResponseEntity.status(403).body("You can only delete your own tasks");
            }
            
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