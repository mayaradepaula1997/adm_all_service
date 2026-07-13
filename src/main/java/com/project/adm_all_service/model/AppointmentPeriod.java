package com.project.adm_all_service.model;

import com.project.adm_all_service.enums.Fortnight;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name =  "tb_appointment_period")
public class AppointmentPeriod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "O campo não pode ser nulo")
    private Integer year;    //year = ano

    @NotNull(message = "O campo não pode ser nulo")
    private Integer month;   //month = mês

    @NotNull(message = "A data de início não pode ser nula")
    @Column(nullable = false)
    private LocalDate startDate;

    @NotNull(message = "A data de final não pode ser nula")
    @Column(nullable = false)
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    private Fortnight fortnight;

    @OneToMany(mappedBy = "appointmentPeriod", cascade = CascadeType.ALL)
    private List<NoteIndicator> noteIndicators = new ArrayList<>();

    //Construtor
    public AppointmentPeriod() {
    }

    public AppointmentPeriod(Long id, Integer year, Integer month, LocalDate startDate, LocalDate endDate, Fortnight fortnight) {
        this.id = id;
        this.year = year;
        this.month = month;
        this.startDate = startDate;
        this.endDate = endDate;
        this.fortnight = fortnight;
    }

    public Long getId() {
        return id;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public Fortnight getFortnight() {
        return fortnight;
    }

    public void setFortnight(Fortnight fortnight) {
        this.fortnight = fortnight;
    }

    public List<NoteIndicator> getNoteIndicators() {
        return noteIndicators;
    }

    public void setNoteIndicators(List<NoteIndicator> noteIndicators) {
        this.noteIndicators = noteIndicators;
    }
}
