package ru.glebova.NauJava.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.glebova.NauJava.adapter.controller.dto.TermPeriodDTO;
import ru.glebova.NauJava.adapter.repository.TermPeriodRepository;
import ru.glebova.NauJava.domain.TermPeriod;
import ru.glebova.NauJava.service.TermPeriodService;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class TermPeriodServiceImpl implements TermPeriodService {
    private final TermPeriodRepository termPeriodRepository;

    @Override
    @Transactional
    public TermPeriod createTermPeriod(TermPeriod dto) {
        log.info("Creating new term period: {} ({} to {})",
                dto.getName(), dto.getStartDate(), dto.getEndDate());

        TermPeriod period = new TermPeriod();
        period.setName(dto.getName());
        period.setStartDate(dto.getStartDate());
        period.setEndDate(dto.getEndDate());

        TermPeriod savedPeriod = termPeriodRepository.save(period);
        log.info("Successfully created term period with ID: {}", savedPeriod.getId());
        return savedPeriod;
    }

    @Override
    @Transactional
    public TermPeriod updateTermPeriod(TermPeriod dto) {
        log.info("Updating term period with ID: {}", dto.getId());

        TermPeriod period = getById(dto.getId());
        log.debug("Current period data: {} ({} to {})",
                period.getName(), period.getStartDate(), period.getEndDate());

        period.setName(dto.getName());
        period.setStartDate(dto.getStartDate());
        period.setEndDate(dto.getEndDate());

        TermPeriod updatedPeriod = termPeriodRepository.save(period);
        log.info("Successfully updated term period with ID: {}", dto.getId());
        log.debug("New period data: {} ({} to {})",
                updatedPeriod.getName(), updatedPeriod.getStartDate(), updatedPeriod.getEndDate());
        return updatedPeriod;
    }

    @Override
    @Transactional(readOnly = true)
    public TermPeriod getById(Long id) {
        log.debug("Fetching term period by ID: {}", id);
        return termPeriodRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Term period not found with ID: {}", id);
                    return new EntityNotFoundException("Period not found");
                });
    }

    @Override
    @Transactional(readOnly = true)
    public List<TermPeriod> getAllPeriods() {
        log.debug("Retrieving all term periods");
        List<TermPeriod> periods = (List<TermPeriod>) termPeriodRepository.findAll();
        log.debug("Found {} term periods", periods.size());
        return periods;
    }
}