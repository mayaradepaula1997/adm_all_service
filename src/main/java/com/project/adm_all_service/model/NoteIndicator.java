package com.project.adm_all_service.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JoinColumnOrFormula;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

//Indicador Apontamento

@Entity
@Table(name = "tb_note_indicator")
public class NoteIndicator {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id")
    private City city;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enterprise_id")
    private Enterprise enterprise;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_period_id")
    private AppointmentPeriod appointmentPeriod;

    @CreationTimestamp  //Preenche automaticamente a data e a hora da criação
    @Column(name = "date_creation", updatable = false)
    private LocalDateTime datecreation;

    @Column(name = "appointment_date", nullable = false)
    private LocalDate appointmentDate;

   @OneToMany(mappedBy = "noteIndicator", cascade = CascadeType.ALL)
    private List<LaunchAppointment> launchAppointments = new ArrayList<>();

    public NoteIndicator() {
    }

    public NoteIndicator(City city, Enterprise enterprise, User createdBy, AppointmentPeriod appointmentPeriod, LocalDateTime datecreation, LocalDate appointmentDate) {
        this.city = city;
        this.enterprise = enterprise;
        this.createdBy = createdBy;
        this.appointmentPeriod = appointmentPeriod;
        this.datecreation = datecreation;
        this.appointmentDate = appointmentDate;
    }

   public Long getId() {
        return id;
    }

   public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }

    public Enterprise getEnterprise() {
        return enterprise;
    }

    public void setEnterprise(Enterprise enterprise) {
        this.enterprise = enterprise;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public AppointmentPeriod getAppointmentPeriod() {
        return appointmentPeriod;
    }

    public void setAppointmentPeriod(AppointmentPeriod appointmentPeriod) {
        this.appointmentPeriod = appointmentPeriod;
    }

    public LocalDateTime getDatecreation() {
        return datecreation;
    }

    public void setDatecreation(LocalDateTime datecreation) {
        this.datecreation = datecreation;
    }

    public LocalDate getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(LocalDate appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public List<LaunchAppointment> getLaunchAppointments() {
        return launchAppointments;
    }

    public void setLaunchAppointments(List<LaunchAppointment> launchAppointments) {
        this.launchAppointments = launchAppointments;
    }
}
