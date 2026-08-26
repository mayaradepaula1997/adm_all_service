package com.project.adm_all_service.model;

import com.project.adm_all_service.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@Entity
@Table(name = "tb_user")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome é obrigatório")
    @Column(name = "name")
    private String name;

    @NotBlank(message = "O e-mail é obrigatório")
    private String email;

    @Column(name = "password")
    private String password;


    @ElementCollection(fetch = FetchType.EAGER) //Esse atributo é uma coleção de elementos simples e não uma entidade
    @Enumerated(EnumType.STRING)                //Essa anotação faz com que o enum seja salvo no BD como texto e não como numero
    @CollectionTable(                           //Cria uma tabela auxiliar
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id")
    )
    @Column(name = "role")
    private Set<Role> roles = new HashSet<>();

    //RELACIONAMENTOS
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_cities",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "city_id")
    )
    private Set<City> cities = new HashSet<>();  //Um usuário pode estar relacionado a várias cidades

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "user_enterprises",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "enterprise_id")
    )
    private Set<Enterprise> enterprises = new HashSet<>();

    @OneToMany(mappedBy = "createdBy", cascade = CascadeType.ALL )
    private List<NoteIndicator> noteIndicators = new ArrayList<>();

    //Construtor Vazio
    public User() {
    }

    //Construtor com argumentos
    public User(String name, String email, Set<Role> roles, String password, Set<City> cities, Set<Enterprise> enterprises) {
        this.name = name;
        this.email = email;
        this.roles = roles;
        this.password = password;
        this.cities = cities;
        this.enterprises = enterprises;
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

    public void setPassword(String password) {
        this.password = password;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    public Set<City> getCities() {
        return cities;
    }

    public void setCities(Set<City> cities) {
        this.cities = cities;
    }

    public Set<Enterprise> getEnterprises() {
        return enterprises;
    }

    public void setEnterprises(Set<Enterprise> enterprises) {
        this.enterprises = enterprises;
    }

    public List<NoteIndicator> getNoteIndicators() {
        return noteIndicators;
    }

    public void setNoteIndicators(List<NoteIndicator> noteIndicators) {
        this.noteIndicators = noteIndicators;
    }

    //Quais roles o usuário possui - Coleção de permissoes
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {   //GrantedAuthority = permissão reconhecida pelo Spring

        return roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                .toList();
    }

    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
