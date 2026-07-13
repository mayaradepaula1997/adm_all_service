package com.project.adm_all_service.controller;

import com.project.adm_all_service.dtos.request.NoteIndicatorRequestDto;
import com.project.adm_all_service.dtos.request.NoteIndicatorUpdateDto;
import com.project.adm_all_service.dtos.response.NoteIndicatorResponseDto;
import com.project.adm_all_service.model.User;
import com.project.adm_all_service.service.NoteIndicatorService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/apontador/note-indicators")
@PreAuthorize("hasAnyRole('ADMIN_MASTER', 'APONTADOR')")
public class NoteIndicatorController {

    private NoteIndicatorService noteIndicatorService;

    public NoteIndicatorController(NoteIndicatorService noteIndicatorService) {
        this.noteIndicatorService = noteIndicatorService;
    }

    @PostMapping
    public ResponseEntity<NoteIndicatorResponseDto> create(@Valid @RequestBody NoteIndicatorRequestDto dto,
                                                           @AuthenticationPrincipal User user){

        NoteIndicatorResponseDto noteIndicator = noteIndicatorService.create(dto, user);

        return ResponseEntity.status(HttpStatus.CREATED).body(noteIndicator);
    }

    @GetMapping
    public ResponseEntity <Page<NoteIndicatorResponseDto>> findAll(@RequestParam (defaultValue = "0") int page,
                                                            @RequestParam (defaultValue = "10") int size){

        Page<NoteIndicatorResponseDto> noteIndicatorResponseDtos = noteIndicatorService.findAll(page, size);

        return ResponseEntity.ok(noteIndicatorResponseDtos);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<NoteIndicatorResponseDto> findById (@PathVariable Long id){

        NoteIndicatorResponseDto responseDto = noteIndicatorService.findById(id);

        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteNoteIndicator (@PathVariable Long id){

        noteIndicatorService.delete(id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<NoteIndicatorResponseDto> updateNoteIndicator(@PathVariable Long id,
                                                                        @Valid @RequestBody NoteIndicatorUpdateDto updateDto){

        NoteIndicatorResponseDto responseDto = noteIndicatorService.update(id, updateDto);

        return ResponseEntity.ok(responseDto);

    }



}

