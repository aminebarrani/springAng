package com.example.stageproj.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "departments")
public class Department {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_dept")
    private Long idDept;
    
    @Column(name = "nom_dept", nullable = false)
    private String nomDept;
    
    @Column(name = "description")
    private String description;
    @OneToMany(mappedBy = "department")
    @com.fasterxml.jackson.annotation.JsonManagedReference
    private java.util.List<Personne> personnes;

    // Default constructor
    public Department() {}
    
    // Constructor with fields
    public Department(String nomDept, String description) {
        this.nomDept = nomDept;
        this.description = description;
    }
    
    // Getters and Setters
    public Long getIdDept() {
        return idDept;
    }
    
    public void setIdDept(Long idDept) {
        this.idDept = idDept;
    }
    
    public String getNomDept() {
        return nomDept;
    }
    
    public void setNomDept(String nomDept) {
        this.nomDept = nomDept;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    @Override
    public String toString() {
        return "Department{" +
                "idDept=" + idDept +
                ", nomDept='" + nomDept + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
} 