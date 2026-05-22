package com.project.adm_all_service.config;

import com.project.adm_all_service.exception.ResourceNotFoundException;
import com.project.adm_all_service.model.User;
import com.project.adm_all_service.repository.UserRepository;
import com.project.adm_all_service.service.AutenticationService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Configuration
public class SecurityFilter extends OncePerRequestFilter { //A cada requisição que houver vai passa por esse classe antes

    private AutenticationService autenticationService;

    private UserRepository userRepository;

    //Construtor
    public SecurityFilter(AutenticationService autenticationService, UserRepository userRepository) {
        this.autenticationService = autenticationService;
        this.userRepository = userRepository;
    }

    //Método que vai extrai o token do HEADER
    public String extractTokenHeader(HttpServletRequest request){

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null){
            return null;
        }

        if (!authHeader.split( " ")[0].equals("Bearer")){  //Se não começar com "Bearer" retorna NULL
            return null;
        }
        return authHeader.split(" ")[1];  //Retorna apenas o token, sem o "Bearer"
    }

    //Esse método é executado automaticamente pelo Spring Security em toda requisição
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        //Se a rota for /authentication, não execute validação JWT
        if (request.getServletPath().equals("/authentication")) { filterChain.doFilter(request, response); return; }

        String token = extractTokenHeader(request); //Chama o método que vai extrair o token

        if (token != null){

            String login = autenticationService.validarTokenJwt(token); //O token é validado é extraido email/login
            User user = userRepository.findByEmail(login)
                    .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado")); //Busca o usuário pelo e-mail

            Authentication autentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(autentication);
        }

        filterChain.doFilter(request, response);

    }
}
