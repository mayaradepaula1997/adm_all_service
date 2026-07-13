package com.project.adm_all_service.controller;

import com.project.adm_all_service.dtos.request.UserCreateDto;
import com.project.adm_all_service.dtos.request.UserUpdateDto;
import com.project.adm_all_service.dtos.response.UserResponseDto;
import com.project.adm_all_service.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @PreAuthorize("isAuthenticated() and hasRole('ADMIN_MASTER')")
    public ResponseEntity<UserResponseDto> create(@RequestBody UserCreateDto createDto){

        UserResponseDto user = userService.create(createDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(user);

    }

    @GetMapping
    @PreAuthorize("isAuthenticated() and hasRole('ADMIN_MASTER')")
    public ResponseEntity <Page<UserResponseDto>> listAll(@RequestParam (defaultValue = "0")int page, @RequestParam (defaultValue = "10")int size){

        Page<UserResponseDto> user = userService.listUser(page, size);

        return ResponseEntity.ok(user);

    }

    @GetMapping(value = "/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponseDto> userById (@PathVariable Long id){

        UserResponseDto user = userService.userById(id);

        return ResponseEntity.ok(user);
    }

    @DeleteMapping(value = "/{id}")
    @PreAuthorize("isAuthenticated() and hasRole('ADMIN_MASTER')")
    public ResponseEntity<Void> deleteUser (@PathVariable Long id){

        userService.deleteUser(id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
    @PutMapping(value = "/{id}")
    @PreAuthorize("isAuthenticated() and hasRole('ADMIN_MASTER')")
    public ResponseEntity<UserResponseDto> update(@PathVariable Long id, @RequestBody UserUpdateDto dto){

        UserResponseDto response = userService.update(id, dto);

        return ResponseEntity.ok(response);
    }
}
