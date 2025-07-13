package com.example.stageproj.repository;

import com.example.stageproj.entity.Personne;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonneRepository extends JpaRepository<Personne, Long> {
    @Query("SELECT DISTINCT p FROM Personne p LEFT JOIN FETCH p.toDoLists")
List<Personne> findAllWithToDoLists();

    @Query("SELECT p FROM Personne p LEFT JOIN FETCH p.toDoLists WHERE p.idPersonne = :id")
    Optional<Personne> findByIdWithToDoLists(@Param("id") Long id);
    
    List<Personne> findByDepartmentIdDept(Long departmentId);
} 