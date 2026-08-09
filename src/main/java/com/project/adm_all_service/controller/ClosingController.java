package com.project.adm_all_service.controller;

import com.project.adm_all_service.dtos.request.GestorSaveDailyValuesRequestDto;
import com.project.adm_all_service.dtos.response.ClosingResponseDto;
import com.project.adm_all_service.service.ClosingService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@PreAuthorize("isAuthenticated()")
public class ClosingController {

    private final ClosingService closingService;

    public ClosingController(ClosingService closingService) {
        this.closingService = closingService;
    }

    /**
     * Gera o fechamento analítico da quinzena.
     * GET /gestor/closing?cityId=1&enterpriseId=1&month=7&year=2026&fortnight=1&dailyValue=100.00
     */
    @GetMapping("/gestor/closing")
    @PreAuthorize("hasAnyRole('ADMIN_MASTER', 'GESTOR', 'RH')")
    public ResponseEntity<ClosingResponseDto> getClosing(
            @RequestParam Long cityId,
            @RequestParam Long enterpriseId,
            @RequestParam Integer month,
            @RequestParam Integer year,
            @RequestParam Integer fortnight,
            @RequestParam(required = false, defaultValue = "0") BigDecimal dailyValue) {

        ClosingResponseDto response = closingService.generateClosing(
                cityId, enterpriseId, month, year, fortnight, dailyValue);

        return ResponseEntity.ok(response);
    }

    /**
     * Salva os valores de diária por dia por colaborador definidos pelo gestor.
     * POST /gestor/closing/daily-values
     */
    @PostMapping("/gestor/closing/daily-values")
    @PreAuthorize("hasAnyRole('ADMIN_MASTER', 'GESTOR', 'RH')")
    public ResponseEntity<Void> saveDailyValues(@RequestBody GestorSaveDailyValuesRequestDto request) {
        closingService.saveDailyValues(request);
        return ResponseEntity.ok().build();
    }

    /**
     * Retorna resumo da quinzena atual de um colaborador.
     * GET /collaborators/{id}/fortnight-summary
     */
    @GetMapping("/collaborators/{id}/fortnight-summary")
    @PreAuthorize("hasAnyRole('ADMIN_MASTER', 'RH', 'APONTADOR')")
    public ResponseEntity<Map<String, Object>> getFortnightSummary(@PathVariable Long id) {
        Map<String, Object> summary = closingService.getCollaboratorFortnightSummary(id);
        return ResponseEntity.ok(summary);
    }
}

