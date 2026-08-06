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

    public UserResponseDto create (UserCreateDto dto, User currentUser){

        //Verificamos se o usuário ja existe
        if (userRepository.findByEmail(dto.email()).isPresent()){
            throw new BusinessException("Usuário já existe");
        }

        // ADMIN_MASTER não pode criar outro ADMIN_MASTER
        if (currentUser.getRoles().contains(Role.ADMIN_MASTER)
                && dto.roleSet().contains(Role.ADMIN_MASTER)) {
            throw new BusinessException("Você não tem permissão para criar um ADMIN_MASTER");
        }

        // Nenhuma rota normal pode criar SUPER_ADMIN
        if (dto.roleSet().contains(Role.SUPER_ADMIN)) {
            throw new BusinessException("Não é permitido criar um SUPER_ADMIN por esta rota");
        }

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

        // Define o adminOwner: o ADMIN_MASTER que está criando fica como dono
        if (currentUser.getRoles().contains(Role.ADMIN_MASTER)) {
            user.setAdminOwner(currentUser);
        }

        //Salva no banco de dados
        User usersaved = userRepository.save(user);

        return toUserResponseDto(usersaved);

    }

    //Lista sub-usuários do ADMIN_MASTER autenticado
    public Page<UserResponseDto> listUser(int page, int size, User currentUser){

        Pageable pageable= PageRequest.of(page, size, Sort.by("name").ascending());

        return userRepository.findByAdminOwner(currentUser, pageable)
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
    public void deleteUser(Long id, User currentUser){

        //Busca o usuário no BD
        User user = userRepository.findById(id).orElseThrow(()-> new ResourceNotFoundException("Usuário não encontrado"));

        // Nunca permite deletar SUPER_ADMIN
        if (user.getRoles().contains(Role.SUPER_ADMIN)) {
            throw new BusinessException("Não é permitido excluir o SUPER_ADMIN");
        }

        // ADMIN_MASTER só pode deletar seus próprios sub-usuários
        if (currentUser.getRoles().contains(Role.ADMIN_MASTER)) {
            if (user.getAdminOwner() == null || !user.getAdminOwner().getId().equals(currentUser.getId())) {
                throw new BusinessException("Você não tem permissão para excluir este usuário");
            }
        }

        userRepository.delete(user);
    }

    //Atualizar usuário
    @Transactional
    public UserResponseDto update(Long id, UserUpdateDto updateDto, User currentUser){

        //Verificar se o usuário existe
        User user = userRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException("Usuário não encontrado"));

        // ADMIN_MASTER só pode editar seus próprios sub-usuários
        if (currentUser.getRoles().contains(Role.ADMIN_MASTER)) {
            if (user.getAdminOwner() == null || !user.getAdminOwner().getId().equals(currentUser.getId())) {
                throw new BusinessException("Você não tem permissão para editar este usuário");
            }
        }

        //Atualizar o nome
        if (updateDto.name() != null && !updateDto.name().isBlank()){
            user.setName(updateDto.name());
        }
        //Atualizar o e-mail e verificar se esse email já existe no BD
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

            if (updateDto.roles().contains(Role.ADMIN_MASTER) || updateDto.roles().contains(Role.SUPER_ADMIN)){
                throw new BusinessException("Não é permitido definir esta role via atualização de usuário");
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