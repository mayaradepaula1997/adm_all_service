package com.project.adm_all_service.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.project.adm_all_service.enums.TypeApontamento;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_appointment")
public class Apontamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Campo obrigatótio")
    @Column(name = "date")
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_appointment")
    private TypeApontamento typeApontamento;

    @Column(name = "observation")
    private String observation;

    @Column(name = "overtime")
    private BigDecimal overtime;

    @NotNull(message = "Campo obrigatótio")
    @Column(name = "creation_date")
    private LocalDateTime creation; //Data de criação

    //RELACIONAMENTOS
    @NotNull(message = "Campo obrigatório")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "collaborator_id")
    private Collaborator collaborator;

    @NotNull(message = "Campo obrigatório")
    @ManyToOne
    @JoinColumn(name = "enterprise_id")
    private Enterprise enterprise;

    @NotNull(message = "Campo obrigatório")
    @ManyToOne
    @JoinColumn(name = "city_id")
    @JsonBackReference
    private City city;

    //CONSTRUTOR
    public Apontamento() {
    }

    public Apontamento(LocalDate date, String observation, TypeApontamento typeApontamento, BigDecimal overtime, LocalDateTime creation, Collaborator collaborator, Enterprise enterprise, City city) {
        this.date = date;
        this.observation = observation;
        this.typeApontamento = typeApontamento;
        this.overtime = overtime;
        this.creation = creation;
        this.collaborator = collaborator;
        this.enterprise = enterprise;
        this.city = city;
    }

    //GETTER AND SETTER
    public Long getId() {
        return id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public TypeApontamento getTypeApontamento() {
        return typeApontamento;
    }

    public void setTypeApontamento(TypeApontamento typeApontamento) {
        this.typeApontamento = typeApontamento;
    }

    public String getObservation() {
        return observation;
    }

    public void setObservation(String observation) {
        this.observation = observation;
    }

    public BigDecimal getOvertime() {
        return overtime;
    }

    public void setOvertime(BigDecimal overtime) {
        this.overtime = overtime;
    }

    public LocalDateTime getCreation() {
        return creation;
    }

    public void setCreation(LocalDateTime creation) {
        this.creation = creation;
    }

    public Collaborator getCollaborator() {
        return collaborator;
    }

    public void setCollaborator(Collaborator collaborator) {
        this.collaborator = collaborator;
    }

    public Enterprise getEnterprise() {
        return enterprise;
    }

    public void setEnterprise(Enterprise enterprise) {
        this.enterprise = enterprise;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }
}
