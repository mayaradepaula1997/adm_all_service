package com.project.adm_all_service.controller;

import com.project.adm_all_service.dtos.request.CityCreateRequestDto;
import com.project.adm_all_service.dtos.request.CityDto;
import com.project.adm_all_service.dtos.request.UpdateCityDto;
import com.project.adm_all_service.dtos.response.CityCreateDto;
import com.project.adm_all_service.service.CityService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/citys")
public class CityController {

   private final CityService cityService;

    public CityController(CityService cityService) {
        this.cityService = cityService;
    }

    //Criação
    @PostMapping
    @PreAuthorize("isAuthenticated() and hasRole('ADMIN_MASTER')") //Proteção do endpoint: Verifica que a o usuário está autenticado e verifica se o usuário tem o perfil de ADMIN_MASTER
    public ResponseEntity<CityCreateDto> createCity(@RequestBody CityCreateRequestDto dto) {

       CityCreateDto createDto = cityService.cityCreate(dto); //Chama o service e guarda seu retorno na variavel

       return ResponseEntity.status(HttpStatus.CREATED).body(createDto);

    }

    //Listar todos
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<CityDto>> listCity(@RequestParam(defaultValue = "0") int page,@RequestParam(defaultValue = "10") int size) {

        Page<CityDto> cityDto = cityService.listCity(page, size);

        return ResponseEntity.ok(cityDto);

    }

    //Cidade por id
    @GetMapping(value = "/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<CityDto> cityById(@PathVariable Long id){

        CityDto cityDto = cityService.cityById(id);

        return ResponseEntity.ok(cityDto);
    }

    //Deletar da cidade
    @DeleteMapping(value = "/{id}")
    @PreAuthorize("isAuthenticated() and hasRole('ADMIN_MASTER')")
    public ResponseEntity<Void> delete (@PathVariable Long id){

        cityService.deleteCity(id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    //Atualização da cidade
    @PutMapping(value = "{id}")
    @PreAuthorize("isAuthenticated() and hasRole('ADMIN_MASTER')")
    public ResponseEntity<CityDto> update(@PathVariable Long id, @RequestBody UpdateCityDto updateCityDto){

        CityDto updateCity = cityService.updateCity(id, updateCityDto);

        return ResponseEntity.ok(updateCity); //Retorna 200 ok
    }







}

