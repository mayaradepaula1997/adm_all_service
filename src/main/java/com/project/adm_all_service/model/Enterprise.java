package com.project.adm_all_service.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "tb_empresa")
public class Enterprise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome é obrigatório")
    private String name;

    @NotBlank(message = "cnpj é obrigatório")
    private String cnpj;

    private LocalDateTime creation;

    //RELACIONAMENTO
    @NotNull(message = "o campo cidade é obrigatorio")
    @ManyToOne(fetch = FetchType.LAZY)         //Só busca a cidade quando precisar
    @JoinColumn(name = "city_id")
    private City city;

    @JsonIgnore
    @OneToMany(mappedBy = "enterprise", cascade = CascadeType.ALL)
    private List<Collaborator> collaborators;

    @JsonIgnore
    @OneToMany(mappedBy = "enterprise", cascade = CascadeType.ALL)
    private List<User> users;

    public Enterprise() {
    }

    public Enterprise(String name, String cnpj, LocalDateTime creation, City city, List<Collaborator> collaborators, List<User> users) {
        this.name = name;
        this.cnpj = cnpj;
        this.creation = creation;
        this.city = city;
        this.collaborators = collaborators;
        this.users = users;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public LocalDateTime getCreation() {
        return creation;
    }

    public void setCreation(LocalDateTime creation) {
        this.creation = creation;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public List<Collaborator> getCollaborators() {
        return collaborators;
    }

    public void setCollaborators(List<Collaborator> collaborators) {
        this.collaborators = collaborators;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
}
