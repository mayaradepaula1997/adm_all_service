package com.project.adm_all_service.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "tb_cidade")
public class City {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome")
    @NotBlank(message = "O nome é obrigatorio")
    private String name;

    @NotBlank(message = "Campo UF é obrigatorio")
    private String uf;

    @Column(name = "data_criacao")
    private LocalDateTime creation; //Data de criação

    //RELACIONAMENTO
    @JsonIgnore
    @OneToMany(mappedBy = "city")
    private List<User> users;        //Uma cidade pode ter varios usuários

    @JsonIgnore             //Evita loop
    @OneToMany(mappedBy = "city")
    private List<Enterprise> enterprises;    //Uma cidade pode ter varias empresas

    @JsonIgnore
    @OneToMany(mappedBy = "city")
    private List<Collaborator> collaborators;

    @OneToMany(mappedBy = "city")
    @JsonManagedReference
    private List<Apontamento> apontamentos;


    //CONSTRUTOR
    public City() {
    }

    public City(String name, String uf, LocalDateTime creation, List<User> users, List<Enterprise> enterprises, List<Collaborator> collaborators, List<Apontamento> apontamentos) {
        this.name = name;
        this.uf = uf;
        this.creation = creation;
        this.users = users;
        this.enterprises = enterprises;
        this.collaborators = collaborators;
        this.apontamentos = apontamentos;
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

    public List<Apontamento> getApontamentos() {
        return apontamentos;
    }

    public void setApontamentos(List<Apontamento> apontamentos) {
        this.apontamentos = apontamentos;
    }
}
