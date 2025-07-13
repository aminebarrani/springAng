package com.example.stageproj.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
@Table(name = "to_do_list")
public class ToDoList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tache")
    private Long idTache;

   @ManyToOne
   @JoinColumn(name = "id_personne")
   @JsonBackReference
   private Personne personne;

    @Column(name = "descrip")
    private String descrip;

    // Default constructor
    public ToDoList() {}

    // Constructor with fields
    public ToDoList(Personne personne, String descrip) {
        this.personne = personne;
        this.descrip = descrip;
    }

    // Getters and Setters
    public Long getIdTache() {
        return idTache;
    }

    public void setIdTache(Long idTache) {
        this.idTache = idTache;
    }

    public Personne getPersonne() {
        return personne;
    }

    public void setPersonne(Personne personne) {
        this.personne = personne;
    }

    public String getDescrip() {
        return descrip;
    }

    public void setDescrip(String descrip) {
        this.descrip = descrip;
    }

    @Override
    public String toString() {
        return "ToDoList{" +
                "idTache=" + idTache +
                ", personne=" + (personne != null ? personne.getIdPersonne() : null) +
                ", descrip='" + descrip + '\'' +
                '}';
    }
} 