package com.bzp.agendadorDeHorarios.infrastructure.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "agendamento")
public class Agendamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String servico;

    private String profissional;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime dataHoraAgendamento;

    private String cliente;

    private String telefoneCliente;

    private LocalDateTime dataInsercao = LocalDateTime.now();

    @PrePersist
    public void prePersist() {
        dataInsercao = LocalDateTime.now();
    }
}