package com.project.adm_all_service.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "tb_users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nome")
    @NotBlank(message = "O nome é obrigatório")
    private String name;

    @NotBlank(message = "O e-mail é obrigatório")
    private String email;

    @Column(name = "senha")
    private String password;

    //Construtor Vazio
    public User() {
    }

    //Construtor com argumentos
    public User(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
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
}
