package com.project.adm_all_service.model;

import com.project.adm_all_service.enums.ClosingStatus;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

//CLASSE DE FECHAMENTO
@Entity
@Table(name = "tb_closing")
public class Closing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Campo obrigatório")
    @Column(name = "month")
    private Integer month;

    @NotNull(message = "Campo obrigatório")
    @Column(name = "year")
    private Integer year;

    @NotNull(message = "Campo obrigatório")
    @Column(name = "fortnight")
    private Integer fortnight;

    @NotNull(message = "Campo obrigatório")
    @Column(name = "default_daily_value")
    private BigDecimal dailyValue;

    @NotNull(message = "Campo obrigatório")
    @Column(name = "closed_at")
    private LocalDateTime closedIn;

    @Column(name = "closing_status")
    @Enumerated(EnumType.STRING)
    private ClosingStatus closingStatus;

    //RELACIONAMENTO
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "enterprise_id")
    @NotNull(message = "Campo obrigatório")
    private Enterprise enterprise;  //Muitos fechamentos estão relacionados a uma empresa

    @ManyToOne
    @JoinColumn(name = "closed_by_user_id")
    @NotNull(message = "Campo obrigatório")
    private User closedBy;     //Muitos fechamentos estão relacionados com um usuário


    // MAP: Coleção de elementos atualizada para inglês
    @ElementCollection               //Sinaliza que é uma lista de valores simples e não uma tabela
    @CollectionTable(
        name = "closing_daily_values" //Criar uma tabela chamada "closing_daily_values"
    )
    @MapKeyColumn(name = "collaborator_id") // ID do colaborador em inglês
    @Column(name = "value") // Nome do valor em inglês
    @Valid
    @NotNull
    private Map<@NotNull Long, @NotNull @Positive BigDecimal> valuesPerEmployee;


    public Closing() {
    }

    public Closing(Integer month, Integer year, Integer fortnight, BigDecimal dailyValue, LocalDateTime closedIn, ClosingStatus closingStatus, Enterprise enterprise, User closedBy, Map<@NotNull Long, @NotNull @Positive BigDecimal> valuesPerEmployee) {
        this.month = month;
        this.year = year;
        this.fortnight = fortnight;
        this.dailyValue = dailyValue;
        this.closedIn = closedIn;
        this.closingStatus = closingStatus;
        this.enterprise = enterprise;
        this.closedBy = closedBy;
        this.valuesPerEmployee = valuesPerEmployee;
    }

    public Long getId() {
        return id;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getFortnight() {
        return fortnight;
    }

    public void setFortnight(Integer fortnight) {
        this.fortnight = fortnight;
    }

    public LocalDateTime getClosedIn() {
        return closedIn;
    }

    public void setClosedIn(LocalDateTime closedIn) {
        this.closedIn = closedIn;
    }

    public BigDecimal getDailyValue() {
        return dailyValue;
    }

    public void setDailyValue(BigDecimal dailyValue) {
        this.dailyValue = dailyValue;
    }

    public ClosingStatus getClosingStatus() {
        return closingStatus;
    }

    public void setClosingStatus(ClosingStatus closingStatus) {
        this.closingStatus = closingStatus;
    }

    public Enterprise getEnterprise() {
        return enterprise;
    }

    public void setEnterprise(Enterprise enterprise) {
        this.enterprise = enterprise;
    }

    public User getClosedBy() {
        return closedBy;
    }

    public void setClosedBy(User closedBy) {
        this.closedBy = closedBy;
    }

    public Map<Long, BigDecimal> getValuesPerEmployee() {
        return valuesPerEmployee;
    }

    public void setValuesPerEmployee(Map<Long, BigDecimal> valuesPerEmployee) {
        this.valuesPerEmployee = valuesPerEmployee;
    }
}
