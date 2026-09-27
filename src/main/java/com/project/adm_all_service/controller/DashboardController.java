package com.project.adm_all_service.controller;

import com.project.adm_all_service.dtos.response.DashboardEvolutionDto;
import com.project.adm_all_service.dtos.response.DashboardSummaryResponseDto;
import com.project.adm_all_service.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/summary")
    @PreAuthorize("hasAnyRole('ADMIN_MASTER', 'SUPER_ADMIN')")
    public ResponseEntity<DashboardSummaryResponseDto> getSummary(
            @RequestParam Integer month,
            @RequestParam Integer year,
            @RequestParam(required = false) Integer fortnight,
            @RequestParam(required = false) List<Long> enterpriseId) {
        
        return ResponseEntity.ok(dashboardService.getSummary(month, year, fortnight, enterpriseId));
    }

    @GetMapping("/evolution")
    @PreAuthorize("hasAnyRole('ADMIN_MASTER', 'SUPER_ADMIN')")
    public ResponseEntity<List<DashboardEvolutionDto>> getEvolution(
            @RequestParam Integer year,
            @RequestParam(required = false) List<Long> enterpriseId) {
        
        return ResponseEntity.ok(dashboardService.getEvolution(year, enterpriseId));
    }

    @GetMapping("/details")
    @PreAuthorize("hasAnyRole('ADMIN_MASTER', 'SUPER_ADMIN')")
    public ResponseEntity<List<com.project.adm_all_service.dtos.response.DashboardCollaboratorExpenseDto>> getDetails(
            @RequestParam Integer month,
            @RequestParam Integer year,
            @RequestParam(required = false) Integer fortnight,
            @RequestParam(required = false) List<Long> enterpriseId) {
        
        return ResponseEntity.ok(dashboardService.getDetails(month, year, fortnight, enterpriseId));
    }
}
