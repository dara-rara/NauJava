package ru.glebova.NauJava.service.impl;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.glebova.NauJava.adapter.controller.dto.ScheduleDTO;
import ru.glebova.NauJava.adapter.repository.custom.CustomScheduleRepository;
import ru.glebova.NauJava.domain.Schedule;
import ru.glebova.NauJava.exception.PupilNotFoundException;
import ru.glebova.NauJava.service.ScheduleService;

import java.util.List;

@Service
@Transactional
public class ScheduleServiceImpl implements ScheduleService {
    private final CustomScheduleRepository customScheduleRepository;

    public ScheduleServiceImpl(CustomScheduleRepository customScheduleRepository) {
        this.customScheduleRepository = customScheduleRepository;
    }

    @Override
    public List<ScheduleDTO> getScheduleClassAndSubject(String className, String subjectName) {
        try {
            if (className == null || className.isBlank()) {
                throw new IllegalArgumentException("Название класса не может быть пустым");
            }
            if (subjectName == null || subjectName.isBlank()) {
                throw new IllegalArgumentException("Название предмета не может быть пустым");
            }
            return customScheduleRepository.findScheduleByClassAndSubject(className, subjectName)
                    .stream()
                    .map(schedule -> new ScheduleDTO(
                            schedule.getDayOfWeek(),
                            schedule.getTime().toString()
                    ))
                    .toList();
        } catch (EntityNotFoundException e) {
            throw new EntityNotFoundException("Ошибка при поиске данных: " + e.getMessage());
        }
    }
}
