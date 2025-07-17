package com.example.stageproj.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "personne")
public class Personne {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_personne")
    private Long idPersonne;

    @Column(name = "nom", nullable = false)
    private String nom;

    @Column(name = "prenom", nullable = false)
    private String prenom;

    @Column(name = "adresse")
    private String adresse;

    @Column(name = "sex")
    private String sex;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    @JoinColumn(name = "id_dept", nullable = false)
    private Department department;

    @OneToMany(mappedBy = "personne", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<ToDoList> toDoLists;

    // Default constructor
    public Personne() {}

    // Constructor with fields
    public Personne(String nom, String prenom, String adresse, String sex, Department department) {
        this.nom = nom;
        this.prenom = prenom;
        this.adresse = adresse;
        this.sex = sex;
        this.department = department;
    }

    // Getters and Setters
    public Long getIdPersonne() {
        return idPersonne;
    }

    public void setIdPersonne(Long idPersonne) {
        this.idPersonne = idPersonne;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    @Override
    public String toString() {
        return "Personne{" +
                "idPersonne=" + idPersonne +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", adresse='" + adresse + '\'' +
                ", sex='" + sex + '\'' +
                ", department=" + (department != null ? department.getIdDept() : null) +
                '}';
    }
}