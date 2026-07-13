package com.project.adm_all_service.model;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "tb_collaborator")
public class Collaborator {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(unique = true, nullable = false) //Anotação para sinalizar que esse é uma campo unico e não pode ser null
    private String cpf;

    @Column(nullable = false)
    private String rg;

    @Column(name = "birth_date", nullable = false)
    private LocalDate date_of_birth;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String pix;

    @CreationTimestamp   //Preencher automaticamente a data e a hora da criação do registro
    @Column(name = "creation_date", updatable = false)
    private LocalDateTime creation; //Data de criação

    //RELACIONAMENTOS
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enterprise_id")
    private Enterprise enterprise;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id")
    private City city;

    @OneToMany(mappedBy = "collaborator", cascade = CascadeType.ALL)
    private List<LaunchAppointment>launchAppointments = new ArrayList<>();

    //CONSTRUTOR
    public Collaborator() {
    }

    public Collaborator(String name, String cpf, String rg, LocalDate date_of_birth, String address, String pix, LocalDateTime creation, Enterprise enterprise, City city) {
        this.name = name;
        this.cpf = cpf;
        this.rg = rg;
        this.date_of_birth = date_of_birth;
        this.address = address;
        this.pix = pix;
        this.creation = creation;
        this.enterprise = enterprise;
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

}
