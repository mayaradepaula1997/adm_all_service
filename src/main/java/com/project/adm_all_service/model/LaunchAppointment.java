package com.project.adm_all_service.model;

import com.project.adm_all_service.enums.StatusLaunch;
import jakarta.persistence.*;

import java.math.BigDecimal;

//LANÇAMENTO APONTAMENTO
@Entity
@Table(name = "tb_launch_appointment")
public class LaunchAppointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "note_indicator_id")
    private NoteIndicator noteIndicator;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "collaborator_id")
    private Collaborator collaborator;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_launch")
    private StatusLaunch statusLaunch;

    private BigDecimal overtime; //Hora Extra

    private String observation;

    //Construtor
    public LaunchAppointment() {
    }

    public LaunchAppointment(NoteIndicator noteIndicator, Collaborator collaborator, StatusLaunch statusLaunch, BigDecimal overtime, String observation) {
        this.noteIndicator = noteIndicator;
        this.collaborator = collaborator;
        this.statusLaunch = statusLaunch;
        this.overtime = overtime;
        this.observation = observation;
    }

    public Long getId() {
        return id;
    }

   public NoteIndicator getNoteIndicator() {
        return noteIndicator;
    }

    public void setNoteIndicator(NoteIndicator noteIndicator) {
        this.noteIndicator = noteIndicator;
    }

    public Collaborator getCollaborator() {
        return collaborator;
    }

    public void setCollaborator(Collaborator collaborator) {
        this.collaborator = collaborator;
    }

    public StatusLaunch getStatusLaunch() {
        return statusLaunch;
    }

    public void setStatusLaunch(StatusLaunch statusLaunch) {
        this.statusLaunch = statusLaunch;
    }

    public BigDecimal getOvertime() {
        return overtime;
    }

    public void setOvertime(BigDecimal overtime) {
        this.overtime = overtime;
    }

    public String getObservation() {
        return observation;
    }

    public void setObservation(String observation) {
        this.observation = observation;
    }
}
