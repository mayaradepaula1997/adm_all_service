package com.project.adm_all_service.model;

import com.project.adm_all_service.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@Entity
@Table(name = "tb_usuário")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome é obrigatório")
    @Column(name = "nome")
    private String name;

    @NotBlank(message = "O e-mail é obrigatório")
    private String email;

    @Column(name = "senha")
    private String password;

    @Enumerated(EnumType.STRING) //Essa anotação faz com que o enum seja salvo no BD como texto e não como numero
    private Role roles;

    //RELACIONAMENTOS
    @NotNull(message = "O campo cidade é obrigatório")
    @ManyToOne
    @JoinColumn(name = "cidade_id") // FK no banco
    private City city;            //Varios usuários estão relacionados a uma cidade


    @NotNull(message = "O campo empresa é obrigatório")
    @ManyToOne
    @JoinColumn(name = "empresa_id")
    private Enterprise enterprise;

    //Construtor Vazio
    public User() {
    }

    //Construtor com argumentos
    public User(Long id, String name, String email, String password, Role roles, City city, Enterprise enterprise) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.roles = roles;
        this.city = city;
        this.enterprise = enterprise;
    }

    //Getter and Setter
    public Long getId() {
        return id;
    }

   public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRoles() {
        return roles;
    }

    public void setRoles(Role roles) {
        this.roles = roles;
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
}
