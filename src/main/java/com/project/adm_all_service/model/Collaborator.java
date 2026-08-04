package com.project.adm_all_service.model;

import com.project.adm_all_service.enums.TransportMode;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Entity
@Table(name = "tb_collaborator")
public class Collaborator {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(unique = true, nullable = false)
    private String cpf;

    @Column(nullable = false)
    private String rg;

    @Column(name = "birth_date", nullable = false)
    private LocalDate date_of_birth;

    @Column(name = "address1", nullable = false, columnDefinition = "varchar(255) default 'Não informado'")
    private String address1;

    @Column(name = "address2", nullable = false, columnDefinition = "varchar(255) default 'Não informado'")
    private String address2;

    @Column(nullable = false)
    private String pix;

    // Filiação - obrigatórios
    @Column(name = "father_name", nullable = false)
    private String fatherName;

    @Column(name = "father_cpf")
    private String fatherCpf;

    @Column(name = "mother_name", nullable = false)
    private String motherName;

    @Column(name = "mother_cpf")
    private String motherCpf;

    // Meio de transporte - opcional
    @Enumerated(EnumType.STRING)
    @Column(name = "transport_mode")
    private TransportMode transportMode;

    @CreationTimestamp
    @Column(name = "creation_date", updatable = false)
    private LocalDateTime creation;

    // RELACIONAMENTOS
    // Multi-empresa: ManyToMany
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "tb_collaborator_enterprise",
            joinColumns = @JoinColumn(name = "collaborator_id"),
            inverseJoinColumns = @JoinColumn(name = "enterprise_id")
    )
    private Set<Enterprise> enterprises = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id")
    private City city;

    @OneToMany(mappedBy = "collaborator", cascade = CascadeType.ALL)
    private List<LaunchAppointment> launchAppointments = new ArrayList<>();

    // CONSTRUTOR
    public Collaborator() {
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

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
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

    public String getFatherName() {
        return fatherName;
    }

    public void setFatherName(String fatherName) {
        this.fatherName = fatherName;
    }

    public String getFatherCpf() {
        return fatherCpf;
    }

    public void setFatherCpf(String fatherCpf) {
        this.fatherCpf = fatherCpf;
    }

    public String getMotherName() {
        return motherName;
    }

    public void setMotherName(String motherName) {
        this.motherName = motherName;
    }

    public String getMotherCpf() {
        return motherCpf;
    }

    public void setMotherCpf(String motherCpf) {
        this.motherCpf = motherCpf;
    }

    public TransportMode getTransportMode() {
        return transportMode;
    }

    public void setTransportMode(TransportMode transportMode) {
        this.transportMode = transportMode;
    }

    public LocalDateTime getCreation() {
        return creation;
    }

    public void setCreation(LocalDateTime creation) {
        this.creation = creation;
    }

    public Set<Enterprise> getEnterprises() {
        return enterprises;
    }

    public void setEnterprises(Set<Enterprise> enterprises) {
        this.enterprises = enterprises;
    }

    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public List<LaunchAppointment> getLaunchAppointments() {
        return launchAppointments;
    }

    public void setLaunchAppointments(List<LaunchAppointment> launchAppointments) {
        this.launchAppointments = launchAppointments;
    }
}
