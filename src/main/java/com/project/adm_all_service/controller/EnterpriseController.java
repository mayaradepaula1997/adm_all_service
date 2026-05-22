package com.project.adm_all_service.controller;

import com.project.adm_all_service.dtos.request.EnterpriseDto;
import com.project.adm_all_service.dtos.request.EnterpriseUpdateDto;
import com.project.adm_all_service.dtos.response.EnterpriseCreateDto;
import com.project.adm_all_service.service.EnterpriseService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/enterprises")
public class EnterpriseController {

    private EnterpriseService enterpriseService;

    public EnterpriseController(EnterpriseService enterpriseService) {
        this.enterpriseService = enterpriseService;
    }

    //Criar
    @PostMapping
    @PreAuthorize("isAuthenticated() and hasRole('ADMIN_MASTER')")
    public ResponseEntity<EnterpriseCreateDto> create(@RequestBody EnterpriseDto dto) {

        EnterpriseCreateDto enterpriseCreateDto = enterpriseService.create(dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(enterpriseCreateDto);

    }

    //Listar todos
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity <Page<EnterpriseDto>> listAllEnterprise(@RequestParam (defaultValue = "0") int
                                                                       page,@RequestParam(defaultValue = "10") int size){
        Page<EnterpriseDto> enterpriseDto = enterpriseService.listAll(page, size);

        return ResponseEntity.ok(enterpriseDto);
    }

    //Listar empresa pelo id
    @GetMapping(value = "/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<EnterpriseDto> enterpriseById(@PathVariable Long id){

        var enterpriseDto = enterpriseService.findById(id);

        return ResponseEntity.ok(enterpriseDto);
    }

    //Deletar empresa
    @DeleteMapping(value = "/{id}")
    @PreAuthorize("isAuthenticated() and hasRole('ADMIN_MASTER')")
    public ResponseEntity<Void> deleteEnterprise(@PathVariable Long id){

        enterpriseService.delete(id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    //Atualizar a empresa
    @PutMapping(value = "/{id}")
    @PreAuthorize("isAuthenticated() and hasRole('ADMIN_MASTER')")
    public ResponseEntity<EnterpriseDto> updateEnterprise (@PathVariable Long id, @RequestBody EnterpriseUpdateDto updateDto){

        EnterpriseDto enterprise = enterpriseService.update(id, updateDto);

        return ResponseEntity.ok(enterprise);
    }




}
