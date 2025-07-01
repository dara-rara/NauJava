package ru.glebova.NauJava.adapter.controller.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class TermPeriodDTO {
    private Long id;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
}
