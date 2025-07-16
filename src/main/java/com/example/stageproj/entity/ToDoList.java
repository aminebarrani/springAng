package com.example.stageproj.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.time.LocalDate;

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

    @Column(name = "datebeb")
    private LocalDate datebeb;

    @Column(name = "datefin")
    private LocalDate datefin;

    // ✅ New field: isChecked
    @Column(name = "ischecked")
    private Boolean isChecked;

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

    public LocalDate getDatebeb() {
        return datebeb;
    }

    public void setDatebeb(LocalDate datebeb) {
        this.datebeb = datebeb;
    }

    public LocalDate getDatefin() {
        return datefin;
    }

    public void setDatefin(LocalDate datefin) {
        this.datefin = datefin;
    }

    public Boolean getIsChecked() {
        return isChecked;
    }

    public void setIsChecked(Boolean isChecked) {
        this.isChecked = isChecked;
    }

    @Override
    public String toString() {
        return "ToDoList{" +
                "idTache=" + idTache +
                ", personne=" + (personne != null ? personne.getIdPersonne() : null) +
                ", descrip='" + descrip + '\'' +
                ", datebeb=" + datebeb +
                ", datefin=" + datefin +
                ", isChecked=" + isChecked +
                '}';
    }
}
