package com.example.stageproj.repository;

import com.example.stageproj.entity.ToDoList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ToDoListRepository extends JpaRepository<ToDoList, Long> {
    // Basic CRUD operations are automatically provided by JpaRepository
    
    List<ToDoList> findByPersonneIdPersonne(Long idPersonne);
} 