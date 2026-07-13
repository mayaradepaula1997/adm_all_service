package com.project.adm_all_service.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tb_city")
public class City {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    @NotBlank(message = "O nome é obrigatorio")
    private String name;

    @NotBlank(message = "Campo UF é obrigatorio")
    private String uf;

    @Column(name = "creation_date", updatable = false)
    private LocalDateTime creation; //Data de criação

    //RELACIONAMENTO
    @JsonIgnore
    @OneToMany(mappedBy = "city", cascade = CascadeType.ALL)
    private List<User> users = new ArrayList<>();        //Uma cidade pode ter varios usuários

    @JsonIgnore             //Evita loop
    @OneToMany(mappedBy = "city")
    private List<Enterprise> enterprises = new ArrayList<>();    //Uma cidade pode ter varias empresas

    @JsonIgnore
    @OneToMany(mappedBy = "city", cascade = CascadeType.ALL)
    private List<Collaborator> collaborators = new ArrayList<>();  //Uma cidade pode ter varias colaboradores

    @OneToMany(mappedBy = "city", cascade = CascadeType.ALL)
    private List<NoteIndicator> noteIndicators = new ArrayList<>();  //Uma cidade pode ter varios apontadores


    //CONSTRUTOR
    public City() {
    }

    public City(String name, String uf, LocalDateTime creation) {
        this.name = name;
        this.uf = uf;
        this.creation = creation;
    }

    @PrePersist //Antes que salvar no banco de dados, vai salvar o momento de criação
    public void prePersist() {
        this.creation = LocalDateTime.now();
    }

   //GETTER E SETTER
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public LocalDateTime getCreation() {
        return creation;
    }

    public void setCreation(LocalDateTime creation) {
        this.creation = creation;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public List<Enterprise> getEnterprises() {
        return enterprises;
    }

    public void setEnterprises(List<Enterprise> enterprises) {
        this.enterprises = enterprises;
    }

    public List<Collaborator> getCollaborators() {
        return collaborators;
    }

    public void setCollaborators(List<Collaborator> collaborators) {
        this.collaborators = collaborators;
    }

    public List<NoteIndicator> getNoteIndicators() {
        return noteIndicators;
    }

    public void setNoteIndicators(List<NoteIndicator> noteIndicators) {
        this.noteIndicators = noteIndicators;
    }
}
