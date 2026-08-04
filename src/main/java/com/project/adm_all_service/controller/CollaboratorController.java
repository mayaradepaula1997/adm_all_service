package com.project.adm_all_service.controller;

import com.project.adm_all_service.dtos.request.CollaboratorRequestDTO;
import com.project.adm_all_service.dtos.request.CollaboratorUpdateDTO;
import com.project.adm_all_service.dtos.response.CollaboratorResponseDTO;
import com.project.adm_all_service.service.CollaboratorService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/collaborators")
public class CollaboratorController {

    private final CollaboratorService collaboratorService;

    public CollaboratorController(CollaboratorService collaboratorService) {
        this.collaboratorService = collaboratorService;
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CollaboratorResponseDTO> create(@Valid @RequestBody CollaboratorRequestDTO dto) {
        CollaboratorResponseDTO collaborator = collaboratorService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(collaborator);
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<CollaboratorResponseDTO>> listAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size,
            @RequestParam(required = false) Long enterpriseId,
            @RequestParam(required = false) Long cityId) {

        Page<CollaboratorResponseDTO> collaborators;

        if (enterpriseId != null && cityId != null) {
            collaborators = collaboratorService.listByEnterpriseAndCity(enterpriseId, cityId, page, size);
        } else if (enterpriseId != null) {
            collaborators = collaboratorService.listByEnterprise(enterpriseId, page, size);
        } else if (cityId != null) {
            collaborators = collaboratorService.listByCity(cityId, page, size);
        } else {
            collaborators = collaboratorService.listAll(page, size);
        }

        return ResponseEntity.ok(collaborators);
    }

    @GetMapping(value = "/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CollaboratorResponseDTO> findById(@PathVariable Long id) {
        CollaboratorResponseDTO collaborator = collaboratorService.findById(id);
        return ResponseEntity.ok(collaborator);
    }

    @DeleteMapping(value = "/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        collaboratorService.deleteCollaborator(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping(value = "/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CollaboratorResponseDTO> update(@PathVariable Long id,
                                                          @Valid @RequestBody CollaboratorUpdateDTO updateDTO) {
        CollaboratorResponseDTO collaboratorUpdate = collaboratorService.updateCollaborator(id, updateDTO);
        return ResponseEntity.ok(collaboratorUpdate);
    }
}
