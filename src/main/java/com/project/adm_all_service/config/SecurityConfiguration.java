package com.project.adm_all_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration  //Define que é uma classe de configuração
@EnableWebSecurity //Ativa o modo de segurança WEB - requisições HTTP
@EnableMethodSecurity  //Habilita a segurança dos métodos
public class SecurityConfiguration {

    private final SecurityFilter securityFilter;

    private final CorsConfigurationSource corsConfigurationSource;  //Quais frontends pode acessar a aplicação

    public SecurityConfiguration(SecurityFilter securityFilter, CorsConfigurationSource corsConfigurationSource) {
        this.securityFilter = securityFilter;
        this.corsConfigurationSource = corsConfigurationSource;
    }

    //Método responsavel pela hierarquia de permissões, o que cada perfil pode acessar (herança de perfil)
    @Bean
    public RoleHierarchy roleHierarchy() {  //Cria e retorna a hierarquia de permissoes (herança de roles)
        return RoleHierarchyImpl.fromHierarchy("""                    
                            
                        ROLE_ADMIN_MASTER > ROLE_RH
                        ROLE_ADMIN_MASTER > ROLE_GESTOR
                        ROLE_ADMIN_MASTER > ROLE_APONTADOR
                       
                   """);

        // ROLE_RH > ROLE_APONTADOR
        // ROLE_GESTOR > ROLE_APONTADOR

    }

    //Quando analisar permissões em métodos, considerar também a hierarquia das roles
    //Ex: @PreAuthorize("hasRole('ADMIN')")
    @Bean
    public MethodSecurityExpressionHandler methodSecurityExpressionHandler(RoleHierarchy roleHierarchy) {
        DefaultMethodSecurityExpressionHandler handler = new DefaultMethodSecurityExpressionHandler();
        handler.setRoleHierarchy(roleHierarchy);
        return handler;
    }


    //Configuração do Spring Secutiry
    //Configuração a seguranças das requisições HTTP
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource))  //Permitir que o frontend acesse a aplicação
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/authentication").permitAll()
                       .requestMatchers("/admin/**").hasRole("ADMIN_MASTER")
                        //.requestMatchers("/rh/**").hasAnyRole("ADMIN_MASTER", "RH")
                       // .requestMatchers("/gestor/**").hasAnyRole("ADMIN_MASTER", "GESTOR")
                        .requestMatchers("/apontador/**").hasAnyRole("ADMIN_MASTER", "APONTADOR")
                        .anyRequest().authenticated()  //Qualquer outra rota precisa estar autenticada

                )



                // FILTRO JWT
                .addFilterBefore(
                        securityFilter,
                        UsernamePasswordAuthenticationFilter.class
                )

                .build();  //Constrói o objeto SecurityFilterChain
    }

    //Médoto que vai está fazendo a criptografia da senha
    @Bean
    public PasswordEncoder passwordEncoder(){

        return new BCryptPasswordEncoder();
    }

    //Método que vai gerenciar a autenticação do Sprink Security, reponsavel por validar (login, senha e autenticar usuário)
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {

        return  authenticationConfiguration.getAuthenticationManager();
    }
}

