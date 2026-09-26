package com.project.adm_all_service.service;

import com.project.adm_all_service.dtos.response.DashboardEvolutionDto;
import com.project.adm_all_service.dtos.response.DashboardExpenseDto;
import com.project.adm_all_service.dtos.response.DashboardSummaryResponseDto;
import com.project.adm_all_service.model.Closing;
import com.project.adm_all_service.repository.ClosingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class DashboardService {

    private final ClosingRepository closingRepository;

    public DashboardService(ClosingRepository closingRepository) {
        this.closingRepository = closingRepository;
    }

    public DashboardSummaryResponseDto getSummary(Integer month, Integer year, Integer fortnight) {
        List<Closing> closings;
        if (fortnight != null) {
            closings = closingRepository.findByMonthAndYearAndFortnight(month, year, fortnight);
        } else {
            closings = closingRepository.findByMonthAndYear(month, year);
        }

        Map<Long, DashboardExpenseDto> expensesMap = new HashMap<>();
        BigDecimal globalTotal = BigDecimal.ZERO;

        for (Closing closing : closings) {
            Long entId = closing.getEnterprise().getId();
            String entName = closing.getEnterprise().getName();
            
            BigDecimal closingTotal = closing.getValuesPerEmployee().values().stream()
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            DashboardExpenseDto existing = expensesMap.getOrDefault(entId, new DashboardExpenseDto(entId, entName, BigDecimal.ZERO));
            expensesMap.put(entId, new DashboardExpenseDto(entId, entName, existing.totalExpense().add(closingTotal)));
            globalTotal = globalTotal.add(closingTotal);
        }

        List<DashboardExpenseDto> sortedExpenses = expensesMap.values().stream()
                .sorted((a, b) -> b.totalExpense().compareTo(a.totalExpense())) // Ordena pelo maior gasto
                .collect(Collectors.toList());

        return new DashboardSummaryResponseDto(sortedExpenses, globalTotal);
    }

    public List<DashboardEvolutionDto> getEvolution(Integer year) {
        List<Closing> closings = closingRepository.findByYear(year);

        // Agrupa por mês e quinzena
        Map<String, List<Closing>> closingsByPeriod = new TreeMap<>();
        
        for (Closing closing : closings) {
            // Ex: "01 - 1ª Quinzena"
            String periodKey = String.format("%02d - %dª Quinzena", closing.getMonth(), closing.getFortnight());
            closingsByPeriod.computeIfAbsent(periodKey, k -> new ArrayList<>()).add(closing);
        }

        List<DashboardEvolutionDto> evolution = new ArrayList<>();

        for (Map.Entry<String, List<Closing>> entry : closingsByPeriod.entrySet()) {
            String periodName = entry.getKey();
            List<Closing> periodClosings = entry.getValue();
            
            if (periodClosings.isEmpty()) continue;
            
            Integer m = periodClosings.get(0).getMonth();
            Integer y = periodClosings.get(0).getYear();
            Integer f = periodClosings.get(0).getFortnight();

            Map<Long, DashboardExpenseDto> expensesMap = new HashMap<>();
            
            for (Closing closing : periodClosings) {
                Long entId = closing.getEnterprise().getId();
                String entName = closing.getEnterprise().getName();
                
                BigDecimal closingTotal = closing.getValuesPerEmployee().values().stream()
                        .filter(Objects::nonNull)
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                DashboardExpenseDto existing = expensesMap.getOrDefault(entId, new DashboardExpenseDto(entId, entName, BigDecimal.ZERO));
                expensesMap.put(entId, new DashboardExpenseDto(entId, entName, existing.totalExpense().add(closingTotal)));
            }

            List<DashboardExpenseDto> expensesList = new ArrayList<>(expensesMap.values());
            evolution.add(new DashboardEvolutionDto(periodName, y, m, f, expensesList));
        }

        return evolution;
    }
}
