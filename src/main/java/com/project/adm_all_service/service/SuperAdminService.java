package com.project.adm_all_service.service;

import com.project.adm_all_service.dtos.request.UserCreateDto;
import com.project.adm_all_service.dtos.request.UserUpdateDto;
import com.project.adm_all_service.dtos.response.EnterpriseSimpleDto;
import com.project.adm_all_service.dtos.response.UserResponseDto;
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

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SuperAdminService {

    private final UserRepository userRepository;
    private final CityRepository cityRepository;
    private final EnterpriseRepository enterpriseRepository;
    private final PasswordEncoder passwordEncoder;

    public SuperAdminService(UserRepository userRepository, CityRepository cityRepository,
                             EnterpriseRepository enterpriseRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.cityRepository = cityRepository;
        this.enterpriseRepository = enterpriseRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Page<UserResponseDto> listAdmins(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        return userRepository.findAdminsByRole(Role.ADMIN_MASTER, pageable)
                .map(this::toDto);
    }

    public UserResponseDto createAdmin(UserCreateDto dto) {
        if (userRepository.findByEmail(dto.email()).isPresent()) {
            throw new BusinessException("E-mail ja cadastrado");
        }

        Set<Role> roles = new HashSet<>();
        roles.add(Role.ADMIN_MASTER);

        City city = null;
        if (dto.cityId() != null) {
            city = cityRepository.findById(dto.cityId())
                    .orElseThrow(() -> new ResourceNotFoundException("Cidade nao encontrada"));
        }

        Set<Enterprise> enterprises = new HashSet<>();
        if (dto.enterpriseIds() != null && !dto.enterpriseIds().isEmpty()) {
            enterprises.addAll(enterpriseRepository.findAllById(dto.enterpriseIds()));
        }

        User admin = new User();
        admin.setName(dto.name());
        admin.setEmail(dto.email());
        admin.setPassword(passwordEncoder.encode(dto.password()));
        admin.setRoles(roles);
        admin.setCity(city);
        admin.setEnterprises(enterprises);

        User saved = userRepository.save(admin);
        return toDto(saved);
    }

    @Transactional
    public UserResponseDto updateAdmin(Long id, UserUpdateDto dto) {
        User admin = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Admin nao encontrado"));

        if (!admin.getRoles().contains(Role.ADMIN_MASTER)) {
            throw new BusinessException("O usuario informado nao e um ADMIN_MASTER");
        }

        if (dto.name() != null && !dto.name().isBlank()) {
            admin.setName(dto.name());
        }

        if (dto.email() != null && !dto.email().isBlank()) {
            Optional<User> emailExist = userRepository.findByEmail(dto.email());
            if (emailExist.isPresent() && !emailExist.get().getId().equals(admin.getId())) {
                throw new BusinessException("E-mail ja cadastrado");
            }
            admin.setEmail(dto.email());
        }

        if (dto.password() != null && !dto.password().isBlank()) {
            admin.setPassword(passwordEncoder.encode(dto.password()));
        }

        if (dto.cityId() != null) {
            City city = cityRepository.findById(dto.cityId())
                    .orElseThrow(() -> new ResourceNotFoundException("Cidade nao encontrada"));
            admin.setCity(city);
        }

        if (dto.enterpriseIds() != null) {
            Set<Enterprise> enterprises = new HashSet<>(enterpriseRepository.findAllById(dto.enterpriseIds()));
            admin.setEnterprises(enterprises);
        }

        User updated = userRepository.save(admin);
        return toDto(updated);
    }

    public void deleteAdmin(Long id) {
        User admin = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Admin nao encontrado"));

        if (!admin.getRoles().contains(Role.ADMIN_MASTER)) {
            throw new BusinessException("O usuario informado nao e um ADMIN_MASTER");
        }

        userRepository.delete(admin);
    }

    private UserResponseDto toDto(User user) {
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