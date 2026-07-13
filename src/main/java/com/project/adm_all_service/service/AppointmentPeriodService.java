package com.project.adm_all_service.service;

import com.project.adm_all_service.dtos.request.PeriodRequestDto;
import com.project.adm_all_service.dtos.response.PeriodResponseDto;
import com.project.adm_all_service.enums.Fortnight;
import com.project.adm_all_service.exception.BusinessException;
import com.project.adm_all_service.mapper.AppointmentPeriodMapper;
import com.project.adm_all_service.model.AppointmentPeriod;
import com.project.adm_all_service.repository.AppointmentPeriodRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;

@Service
public class AppointmentPeriodService {

    private final AppointmentPeriodRepository appointmentPeriodRepository;

    public AppointmentPeriodService(AppointmentPeriodRepository appointmentPeriodRepository) {
        this.appointmentPeriodRepository = appointmentPeriodRepository;
    }

   /*//CRIAR PERIODO
    @Transactional
    public PeriodResponseDto create(PeriodRequestDto dto){

        //Verificar se existe um periodo com o mesmo ano, mês e quinzena
        if (appointmentPeriodRepository.existsByYearAndMonthAndFortnight(
                dto.year(),
                dto.month(),
                dto.fortnight())){
            throw  new BusinessException("Este periodo já está cadastrado");
        }

        //Mapper que instancia a entidade
        AppointmentPeriod appointmentPeriod = AppointmentPeriodMapper.toEntity(dto);

        calculateDate(appointmentPeriod);

        //Salva a entidade no BD
        AppointmentPeriod saved = appointmentPeriodRepository.save(appointmentPeriod);

        //Retornamos o DTO
        return AppointmentPeriodMapper.toDto(saved);

    }

    //LISTAR OS PERIODOS
    public Page<PeriodResponseDto> findAll(int page, int size){

        Pageable pageable = PageRequest.of(page, size);

        return appointmentPeriodRepository.findAll(pageable)
                .map(appointmentPeriod -> AppointmentPeriodMapper.toDto(appointmentPeriod));

    }

    //LISTAGEM UM PERIODO PELO  ID
    public PeriodResponseDto findById (Long id){

        AppointmentPeriod appointmentPeriod = appointmentPeriodRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Período não encontrado."));

        return AppointmentPeriodMapper.toDto(appointmentPeriod);

    }
    //DELETAR UM PERIODO PELO ID
    @Transactional
    public void deletePeriod (Long id){

        //Buscamos o periodo no BD
        AppointmentPeriod appointmentPeriod = appointmentPeriodRepository.findById(id)
                .orElseThrow(()-> new BusinessException("Período não encontrado."));

        appointmentPeriodRepository.delete(appointmentPeriod);
    }

    //ATUALIZAR  PERIODO PELO ID
    @Transactional
    public PeriodResponseDto updatePeriod (Long id, PeriodRequestDto dto) {

        //Buscamos o periodo pelo id no BD
        AppointmentPeriod appointmentPeriod = appointmentPeriodRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Período não encontrado."));

        appointmentPeriodRepository.findByYearAndMonthAndFortnight(dto.year(), dto.month(), dto.fortnight())
                .ifPresent(period -> {
                    if (!period.getId().equals(id)) {
                        throw new BusinessException("Este período já está cadastrado.");
                    }
                });

        //Atualiza os dados
        appointmentPeriod.setYear(dto.year());
        appointmentPeriod.setMonth(dto.month());
        appointmentPeriod.setFortnight(dto.fortnight());

        //Método que vai recalcular a data inicio e fim
        calculateDate(appointmentPeriod);

        //Salva no banco e dados
        AppointmentPeriod updatePeriod = appointmentPeriodRepository.save(appointmentPeriod);

        return AppointmentPeriodMapper.toDto(updatePeriod);
    }*/

    //Recebe a data e a quinzena
    @Transactional
    public AppointmentPeriod findOrCreate(LocalDate appointmentDate, Fortnight fortnight) {

       Integer year = appointmentDate.getYear();
       Integer month = appointmentDate.getMonthValue();

       return findOrCreate(year, month, fortnight);

    }

    // Busca um período pelo ano, mês e quinzena
    // Caso não exista, cria automaticamente um novo período,calcula as datas de início e fim da quinzena e o salva no banco
    @Transactional
    public AppointmentPeriod findOrCreate(
            Integer year,
            Integer month,
            Fortnight fortnight) {

        return appointmentPeriodRepository
                .findByYearAndMonthAndFortnight(year, month, fortnight)
                .orElseGet(() -> {

                    AppointmentPeriod period = new AppointmentPeriod();

                    period.setYear(year);
                    period.setMonth(month);
                    period.setFortnight(fortnight);

                    calculateDate(period);

                    return appointmentPeriodRepository.save(period);
                });
    }

    //MÉTODO QUE VAI CALCULAR AUTOMATICAMENTE A DATA INICIO E DATA FINAL DA QUINZENA
    private void calculateDate(AppointmentPeriod appointmentPeriod) {

        Integer year = appointmentPeriod.getYear();     //obtemos o ano
        Integer month = appointmentPeriod.getMonth();   //obtemos o mês

        //Classe de datas do Java, que representa o ano e mês
        YearMonth yearMonth = YearMonth.of(year, month);

        if (appointmentPeriod.getFortnight() == Fortnight.FIRST) {

            appointmentPeriod.setStartDate(LocalDate.of(year, month, 1));

            appointmentPeriod.setEndDate(LocalDate.of(year, month, 15));

        } else {

            appointmentPeriod.setStartDate(LocalDate.of(year, month, 16));

            appointmentPeriod.setEndDate(yearMonth.atEndOfMonth());   //yearMonth sabe exatamente quantos dias cada mês possui
        }

    }

}
