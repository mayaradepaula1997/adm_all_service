package com.project.adm_all_service.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "tb_collaborator")
public class Collaborator {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome é obrigatorio")
    @Column(name = "name")
    private String name;

    @NotBlank(message = "O cpf é obrigatorio")
    private String cpf;

    private String rg;

    @Column(name = "birth_date")
    private LocalDate date_of_birth;

    @Column(name = "address")
    private String address;

    @NotBlank(message = "Informe a chave pix")
    private String pix;

    @Column(name = "creation_date")
    private LocalDateTime creation; //Data de criação

    //RELACIONAMENTOS
    @NotNull(message = "Informe a empresa")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enterprise_id")
    private Enterprise enterprise;

    @NotNull(message = "Informe a cidade")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id")
    private City city;

    @JsonIgnore
    @OneToMany(mappedBy = "collaborator", cascade = CascadeType.ALL)
    private List<Apontamento> appointments;


    //CONSTRUTOR
    public Collaborator() {
    }

    public Collaborator(String name, String cpf, String rg, LocalDate date_of_birth, String address, String pix, LocalDateTime creation, Enterprise enterprise, City city, List<Apontamento> appointments) {
        this.name = name;
        this.cpf = cpf;
        this.rg = rg;
        this.date_of_birth = date_of_birth;
        this.address = address;
        this.pix = pix;
        this.creation = creation;
        this.enterprise = enterprise;
        this.city = city;
        this.appointments = appointments;
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

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getRg() {
        return rg;
    }

    public void setRg(String rg) {
        this.rg = rg;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public LocalDate getDate_of_birth() {
        return date_of_birth;
    }

    public void setDate_of_birth(LocalDate date_of_birth) {
        this.date_of_birth = date_of_birth;
    }

    public String getPix() {
        return pix;
    }

    public void setPix(String pix) {
        this.pix = pix;
    }

    public LocalDateTime getCreation() {
        return creation;
    }

    public void setCreation(LocalDateTime creation) {
        this.creation = creation;
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

    public List<Apontamento> getAppointments() {
        return appointments;
    }

    public void setAppointments(List<Apontamento> appointments) {
        this.appointments = appointments;
    }
}
