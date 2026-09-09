package com.bzp.agendadorDeHorarios.services;

import com.bzp.agendadorDeHorarios.infrastructure.entity.Agendamento;
import com.bzp.agendadorDeHorarios.infrastructure.repository.AgendamentoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AgendamentoService {

    private final AgendamentoRepository agendamentoRepository;

    public Agendamento salvarAgendamento(Agendamento agendamento){

        LocalDateTime horaAgendamento = agendamento.getDataHoraAgendamento()
                .withSecond(0)
                .withNano(0);

        agendamento.setDataHoraAgendamento(horaAgendamento);

        if (horaAgendamento.getMinute() != 0) {
            throw new RuntimeException("Os agendamentos devem ser feitos em horários cheios (ex: 11:00, 12:00).");
        }

        LocalDateTime horaFim = horaAgendamento.plusMinutes(59);

        List<Agendamento> conflitos = agendamentoRepository.findByDataHoraAgendamentoBetween(
                horaAgendamento,
                horaFim
        );

        if(!conflitos.isEmpty()){
            throw new RuntimeException("Horário já está preenchido");
        }

        return agendamentoRepository.save(agendamento);
    }

    public void deletarAgendamento(LocalDateTime dataHoraAgendamento, String cliente){
        agendamentoRepository.deleteByDataHoraAgendamentoAndCliente(dataHoraAgendamento, cliente);
    }

    public List<Agendamento> buscarAgendamentosDia(LocalDate data){
        LocalDateTime primeiraHoraDia = data.atStartOfDay();
        LocalDateTime horaFinalDia = data.atTime(23, 59, 59);

        return agendamentoRepository.findByDataHoraAgendamentoBetween(primeiraHoraDia, horaFinalDia);
    }

    public Agendamento alterarAgendamento(
            Agendamento agendamento,
            String cliente,
            LocalDateTime dataHoraAgendamentoOriginal) {

        Agendamento agendaOriginal =
                agendamentoRepository.findByDataHoraAgendamentoAndCliente(
                        dataHoraAgendamentoOriginal,
                        cliente
                );

        if (Objects.isNull(agendaOriginal)) {
            throw new RuntimeException("Agendamento original não encontrado");
        }

        LocalDateTime novaHoraAgendamento = agendamento.getDataHoraAgendamento()
                .withSecond(0)
                .withNano(0);

        agendamento.setDataHoraAgendamento(novaHoraAgendamento);

        if (novaHoraAgendamento.getMinute() != 0) {
            throw new RuntimeException("Os agendamentos devem ser feitos em horários cheios (ex: 11:00, 12:00).");
        }

        LocalDateTime novaHoraFim = novaHoraAgendamento.plusMinutes(59);

        List<Agendamento> conflitos = agendamentoRepository.findByDataHoraAgendamentoBetween(
                novaHoraAgendamento,
                novaHoraFim
        );

        boolean temConflito = conflitos.stream()
                .anyMatch(conflito -> !conflito.getId().equals(agendaOriginal.getId()));

        if (temConflito) {
            throw new RuntimeException("O novo horário escolhido já está preenchido");
        }

        agendamento.setId(agendaOriginal.getId());
        agendamento.setDataInsercao(agendaOriginal.getDataInsercao());

        return agendamentoRepository.save(agendamento);
    }
}