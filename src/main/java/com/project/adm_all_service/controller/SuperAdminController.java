package com.project.adm_all_service.controller;

import com.project.adm_all_service.dtos.request.UserCreateDto;
import com.project.adm_all_service.dtos.request.UserUpdateDto;
import com.project.adm_all_service.dtos.response.UserResponseDto;
import com.project.adm_all_service.service.SuperAdminService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/super-admin/users")
public class SuperAdminController {

    private final SuperAdminService superAdminService;

    public SuperAdminController(SuperAdminService superAdminService) {
        this.superAdminService = superAdminService;
    }

    @GetMapping
    @PreAuthorize("isAuthenticated() and hasRole('SUPER_ADMIN')")
    public ResponseEntity<Page<UserResponseDto>> listAdmins(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Page<UserResponseDto> admins = superAdminService.listAdmins(page, size);
        return ResponseEntity.ok(admins);
    }

    @PostMapping
    @PreAuthorize("isAuthenticated() and hasRole('SUPER_ADMIN')")
    public ResponseEntity<UserResponseDto> createAdmin(@RequestBody UserCreateDto dto) {
        UserResponseDto created = superAdminService.createAdmin(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated() and hasRole('SUPER_ADMIN')")
    public ResponseEntity<UserResponseDto> updateAdmin(
            @PathVariable Long id,
            @RequestBody UserUpdateDto dto) {
        UserResponseDto updated = superAdminService.updateAdmin(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated() and hasRole('SUPER_ADMIN')")
    public ResponseEntity<Void> deleteAdmin(@PathVariable Long id) {
        superAdminService.deleteAdmin(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}