package com.project.adm_all_service.config;

import com.project.adm_all_service.enums.Role;
import com.project.adm_all_service.model.City;
import com.project.adm_all_service.model.Enterprise;
import com.project.adm_all_service.model.User;
import com.project.adm_all_service.repository.CityRepository;
import com.project.adm_all_service.repository.EnterpriseRepository;
import com.project.adm_all_service.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;

@Configuration
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CityRepository cityRepository;
    private final EnterpriseRepository enterpriseRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository, CityRepository cityRepository, EnterpriseRepository enterpriseRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.cityRepository = cityRepository;
        this.enterpriseRepository = enterpriseRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // Verifica se já existe algum usuário no banco para evitar duplicidade de carga
        if (userRepository.count() == 0) {
            
            // 1. Cria uma cidade padrão (Necessário para cadastrar a Empresa e Usuário)
            City city = new City("Sede Principal", "SP", LocalDateTime.now());
            city = cityRepository.save(city);

            // 2. Cria uma empresa padrão
            Enterprise enterprise = new Enterprise(null, "Empresa Principal", "00000000000000", LocalDateTime.now(), city);
            enterprise = enterpriseRepository.save(enterprise);

            // 3. Cria o usuário Admin
            User admin = new User(
                    "Administrador Master",
                    "admin@admallservice.com",
                    Set.of(Role.ADMIN_MASTER),
                    passwordEncoder.encode("admin123"), // Senha encriptada
                    city,
                    Set.of(enterprise)
            );

            userRepository.save(admin);
            System.out.println("====== USUÁRIO ADMIN CRIADO COM SUCESSO ======");
            System.out.println("Email: admin@admallservice.com");
            System.out.println("Senha: admin123");
            System.out.println("===============================================");
        }
    }
}
