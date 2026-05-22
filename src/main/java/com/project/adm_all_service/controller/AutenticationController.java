package com.project.adm_all_service.controller;


import com.project.adm_all_service.dtos.request.AuthDto;
import com.project.adm_all_service.service.AutenticationService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/authentication")
public class AutenticationController {

    private AutenticationService autenticationService; //Regras de negócio, geração de token JWT, validações

    private AuthenticationManager authenticationManager; //Componente do Spring Security, responsavel por autenticar o usuário

    public AutenticationController(AutenticationService autenticationService, AuthenticationManager authenticationManager) {
        this.autenticationService = autenticationService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping
    public String auth(@RequestBody AuthDto authDto){

        //Representa as credencias do usuário/login (email e senha), o usuário não está autenticado ele apenas carrega os dados digitados
        UsernamePasswordAuthenticationToken userAutenticationToken = new UsernamePasswordAuthenticationToken(authDto.email(), authDto.passwordUser());

        //Vamos tentar autenticar o usuário e com isso vamos chamar o UserDetailsService, se deu certo o usuário fica autenticado
        authenticationManager.authenticate(userAutenticationToken);

        //Vamos o retornar o token, para o usuário autenticado
        return autenticationService.obterToken(authDto);

    }

}
