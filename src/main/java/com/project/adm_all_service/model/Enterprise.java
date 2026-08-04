package com.project.adm_all_service.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "tb_enterprise")
public class Enterprise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome é obrigatório")
    private String name;

    // Aceita CPF ou CNPJ
    @NotBlank(message = "Documento (CPF ou CNPJ) é obrigatório")
    @Column(name = "documento", unique = true)
    private String documento;

    @CreationTimestamp
    @Column(name = "creation_date", updatable = false)
    private LocalDateTime creation;

    // RELACIONAMENTO
    @NotNull(message = "o campo cidade é obrigatorio")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id")
    private City city;

    @JsonIgnore
    @ManyToMany(mappedBy = "enterprises")
    private Set<Collaborator> collaborators = new HashSet<>();

    @JsonIgnore
    @ManyToMany(mappedBy = "enterprises")
    private Set<User> users = new HashSet<>();

    @OneToMany(mappedBy = "enterprise", cascade = CascadeType.ALL)
    private List<NoteIndicator> noteIndicators = new ArrayList<>();

    public Enterprise() {
    }

    public Enterprise(Long id, String name, String documento, LocalDateTime creation, City city) {
        this.id = id;
        this.name = name;
        this.documento = documento;
        this.creation = creation;
        this.city = city;
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

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
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

    public Set<Collaborator> getCollaborators() {
        return collaborators;
    }

    public void setCollaborators(Set<Collaborator> collaborators) {
        this.collaborators = collaborators;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    public List<NoteIndicator> getNoteIndicators() {
        return noteIndicators;
    }

    public void setNoteIndicators(List<NoteIndicator> noteIndicators) {
        this.noteIndicators = noteIndicators;
    }
}
