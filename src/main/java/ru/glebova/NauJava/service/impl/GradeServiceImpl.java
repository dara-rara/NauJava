package ru.glebova.NauJava.service.impl;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.glebova.NauJava.adapter.controller.dto.GradeDTO;
import ru.glebova.NauJava.adapter.repository.GradeRepository;
import ru.glebova.NauJava.adapter.repository.PupilRepository;
import ru.glebova.NauJava.adapter.repository.SubjectRepository;
import ru.glebova.NauJava.adapter.repository.custom.CustomGradeRepository;
import ru.glebova.NauJava.domain.Grade;
import ru.glebova.NauJava.domain.Pupil;
import ru.glebova.NauJava.domain.Subject;
import ru.glebova.NauJava.exception.PupilNotFoundException;
import ru.glebova.NauJava.service.GradeService;

import java.util.List;

@Service
@Transactional
public class GradeServiceImpl implements GradeService {
    private final PupilRepository pupilRepository;
    private final SubjectRepository subjectRepository;
    private final CustomGradeRepository customGradeRepository;
    private final GradeRepository gradeRepository;

    public GradeServiceImpl(PupilRepository pupilRepository, SubjectRepository subjectRepository,
                            CustomGradeRepository customGradeRepository, GradeRepository gradeRepository) {
        this.pupilRepository = pupilRepository;
        this.subjectRepository = subjectRepository;
        this.customGradeRepository = customGradeRepository;
        this.gradeRepository = gradeRepository;
    }

    @Override
    public List<GradeDTO> getGradesPupilAndSubject(Long pupilId, Long subjectId) {
        Pupil pupil = pupilRepository.findById(pupilId)
                .orElseThrow(() -> new PupilNotFoundException(pupilId));

        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new EntityNotFoundException("Предмет с ID " + subjectId + " не найден"));

        return customGradeRepository.findGradesByPupilAndSubject(pupil, subject)
                .stream()
                .map(grade -> new GradeDTO(
                        pupil.getUser().getFirstName() + " " + pupil.getUser().getLastName(),
                        subject.getName(),
                        grade.getGrade(),
                        grade.getDate().toString()
                ))
                .toList();
    }

    @Override
    public List<Grade> getGradesPupil(Long pupilId) {
        Pupil pupil = pupilRepository.findById(pupilId)
                .orElseThrow(() -> new PupilNotFoundException(pupilId));
        List<Grade> grades = gradeRepository.findByPupil(pupil);
        return grades;
    }
}
