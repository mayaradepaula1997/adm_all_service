package com.project.adm_all_service.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.project.adm_all_service.dtos.request.AuthDto;
import com.project.adm_all_service.enums.Role;
import com.project.adm_all_service.exception.BusinessException;
import com.project.adm_all_service.model.User;
import com.project.adm_all_service.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class AutenticationService implements UserDetailsService {

    private UserRepository userRepository;

    public AutenticationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    //Método é chamado quando alguem tenta se autenticar, buscamos o usuário no BD que implemetar o UserDetails e retorna o Authentication
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException { //Recebe o e-mail digitado no login
        return userRepository.findByEmail(email)                                           //Busca o usuário no BD e retorna um usuário autenticavel
                .orElseThrow(() ->
                        new UsernameNotFoundException("Usuário não encontrado")
                );

    }


    //Método para gerar o token JWT, recebe como parametro um USUÁRIO
    public String gerarTokenJwt(User user){

        try{
            //Cria a assinatura "Secret":É uma parte unica do nosso token, para incrementar - DEFINIR NA APLICAÇÃO
            Algorithm algorithm = Algorithm.HMAC256("my-secret"); //Garante que apenas seu servidor consiga gerar e validar o token

            //Configurações para gerar o token
            String token = JWT.create()
                    .withIssuer("adm_all_service")            //Quem gerou o token -foi gerado pela minha aplicação
                    .withSubject(user.getEmail())             //Quem é o dono do token
                    .withClaim("userId",user.getId())   //Coloca o ID do usuário no token, carrega o userId, sem precisar buscar no BD
                    .withClaim(
                            "roles",                    //Acrescenta no token a Role do usuário autenticado
                            user.getRoles()                  //Pega as Roles
                                    .stream()
                                    .map(Role::name)        //Converte o ENUM em String
                                    .toList()
                    )
                    .withExpiresAt(gerarDataExpiracao())     //Tempo de inspiração do token
                    .sign(algorithm);                        //Assinatura com  algoritmo da chave secreta

            return token;

        } catch(JWTCreationException e){
            throw  new BusinessException("Erro ao tentar gerar o token! " + e.getMessage());
        }
    }


    //Método para obter o token, passando o email e senha
    public String obterToken (AuthDto authDTo){

        User user = userRepository.findByEmail(authDTo.email())
                .orElseThrow(()-> new RuntimeException("Usuário não encontrado"));

        return gerarTokenJwt(user);   //Chama o método que gera o token
    }


    //Método que vai validar se o token enviado é valido, esse método pode lançar um exceção
    public String validarTokenJwt (String token){  //Esse método retorna o login do usuario

        try {
            Algorithm algorithm = Algorithm.HMAC256("my-secret");

            return JWT.require(algorithm)
                    .withIssuer("adm_all_service")
                    .build()
                    .verify(token)
                    .getSubject();

        } catch (JWTVerificationException e){
            throw new BusinessException("Token não é valido");

        }
    }


    //Método que gera o tempo de inspiração do token
    private Instant gerarDataExpiracao() {

        return LocalDateTime.now()   //Chama a hora atual
                .plusHours(8)        //Adiciona mais 8 horas
                .toInstant(ZoneOffset.of("-03:00"));  //Converte o time zone
    }
}

