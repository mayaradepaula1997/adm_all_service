package com.project.adm_all_service.service;

import com.project.adm_all_service.dtos.request.UserCreateDto;
import com.project.adm_all_service.dtos.request.UserUpdateDto;
import com.project.adm_all_service.dtos.response.UserResponseDto;
import com.project.adm_all_service.dtos.response.EnterpriseSimpleDto;
import java.util.stream.Collectors;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import com.project.adm_all_service.enums.Role;
import com.project.adm_all_service.exception.BusinessException;
import com.project.adm_all_service.exception.ResourceNotFoundException;
import com.project.adm_all_service.model.City;
import com.project.adm_all_service.model.Enterprise;
import com.project.adm_all_service.model.User;
import com.project.adm_all_service.repository.CityRepository;
import com.project.adm_all_service.repository.EnterpriseRepository;
import com.project.adm_all_service.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {

    private  final UserRepository userRepository;

    private final CityRepository cityRepository;

    private final EnterpriseRepository enterpriseRepository;

    private final PasswordEncoder passwordEncoder;


    public UserService(UserRepository userRepository, CityRepository cityRepository, EnterpriseRepository enterpriseRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.cityRepository = cityRepository;
        this.enterpriseRepository = enterpriseRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponseDto create (UserCreateDto dto){

        //Verificamos se o usuário ja existe
        if (userRepository.findByEmail(dto.email()).isPresent()){
            throw new BusinessException("Usuário já existe");        }


        //Verificar se a cidade já existe
        City city = null;
        if (dto.cityId() != null) {
            city = cityRepository.findById(dto.cityId())
                    .orElseThrow(()-> new ResourceNotFoundException("Cidade não encontrada"));
        }

        //Verificar se as empresas já existem
        Set<Enterprise> enterprises = new HashSet<>();
        if (dto.enterpriseIds() != null && !dto.enterpriseIds().isEmpty()) {
            enterprises.addAll(enterpriseRepository.findAllById(dto.enterpriseIds()));
        }

        //Instância a classe usuário
        User user = new User();
        user.setName(dto.name());
        user.setEmail(dto.email());
        user.setPassword(passwordEncoder.encode(dto.password()));
        user.setRoles(dto.roleSet());
        user.setCity(city);
        user.setEnterprises(enterprises);

        //Salva no banco de dados
        User usersaved = userRepository.save(user);

        return toUserResponseDto(usersaved);

    }

    //Listar todos os usuários
    public Page<UserResponseDto> listUser(int page, int size ){

        Pageable pageable= PageRequest.of(page, size, Sort.by("name").ascending());

        return userRepository.findAll(pageable)
                .map(this::toUserResponseDto);
    }

    //Listar usuário pelo id
    public UserResponseDto userById(Long id){

        //Busca o usuário no banco de dados
        User user = userRepository.findById(id).
                orElseThrow(()-> new ResourceNotFoundException("Usuário não encontrado"));

        return toUserResponseDto(user);
    }

    //Deletar o usuário
    public void deleteUser(Long id){

        //Busca o usuário no BD
        User user = userRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Usuário não encontrado"));

        userRepository.delete(user);
    }

    //Atualizar usuário
    @Transactional
    public UserResponseDto update(Long id, UserUpdateDto updateDto){

        System.out.println("Entrou no update");
        System.out.println("ID: " + id);
        System.out.println("Nome: " + updateDto.name());
        System.out.println("Email: " + updateDto.email());

        //Verificar se o usuário existe
        User user = userRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Usuário não encontrado"));

        //Atualizar o nome
        if (updateDto.name() != null && !updateDto.name().isBlank()){
            user.setName(updateDto.name());
        }
        //Atuslizar o e-mail e verificar se esse email já existe no BD
        if (updateDto.email() != null && !updateDto.email().isBlank()){

            //Verifica se já tem um usuário com esse e-mail
            Optional<User> emailExist = userRepository.findByEmail(updateDto.email());

            //Se encontrar o e-mail, vamos verificar se esse e-mail é diferente do usuária atual
            if (emailExist.isPresent() && !emailExist.get().getId().equals(user.getId())){
                throw  new BusinessException("E-mail já cadastrado");
        }
            user.setEmail(updateDto.email());
    }
        //Atualizar a senha
        if (updateDto.password() != null && !updateDto.password().isBlank()){
            user.setPassword(passwordEncoder.encode(updateDto.password()));
        }

        //Atualizar role
        if (updateDto.roles() != null && !updateDto.roles().isEmpty()){

            if (updateDto.roles().contains(Role.ADMIN_MASTER)){
                throw new BusinessException("Não é permitido definir ADMIN_MASTE");
            }

            user.setRoles(updateDto.roles());
        }

        //Atualizar a cidade
        if (updateDto.cityId() != null){

            City city = cityRepository.findById(updateDto.cityId())
                    .orElseThrow(()-> new BusinessException("Cidade não encontrada"));

            user.setCity(city);
        }

        //Atualizar a empresa
        if(updateDto.enterpriseIds() != null){
            Set<Enterprise> enterprises = new HashSet<>(enterpriseRepository.findAllById(updateDto.enterpriseIds()));
            user.setEnterprises(enterprises);
        }

        // Clear city and enterprise if the user is not restricted to a location
        if (user.getRoles().contains(Role.RH) || user.getRoles().contains(Role.ADMIN_MASTER)) {
            user.setCity(null);
            user.setEnterprises(new HashSet<>());
        }

       //Salva os dados atualizados no banco de dados
        User userUpdate = userRepository.save(user);

        return toUserResponseDto(userUpdate);
    }

    private UserResponseDto toUserResponseDto(User user) {
        List<EnterpriseSimpleDto> entDtos = user.getEnterprises().stream()
                .map(e -> new EnterpriseSimpleDto(e.getId(), e.getName(), e.getDocumento()))
                .collect(Collectors.toList());

        return new UserResponseDto(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRoles(),
                user.getCity() != null ? user.getCity().getId() : null,
                user.getCity() != null ? user.getCity().getName() : null,
                entDtos
        );
    }
}