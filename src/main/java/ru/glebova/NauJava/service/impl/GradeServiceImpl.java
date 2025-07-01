package ru.glebova.NauJava.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.glebova.NauJava.adapter.controller.dto.*;
import ru.glebova.NauJava.adapter.repository.GradeRepository;
import ru.glebova.NauJava.adapter.repository.PeriodScheduleRepository;
import ru.glebova.NauJava.domain.*;
import ru.glebova.NauJava.service.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class GradeServiceImpl implements GradeService {
    private final GradeRepository gradeRepository;
    private final PupilService pupilService;
    private final TeacherService teacherService;
    private final SubjectService subjectService;
    private final ClassesService classesService;
    private final TermPeriodService termPeriodService;
    private final PeriodLessonService periodLessonService;
    private final PeriodScheduleRepository periodScheduleRepository;

    @Override
    @Transactional(readOnly = true)
    public GradeTableDTO prepareGradeTableForTeacher(GradeSetupDTO request) {
        log.info("Preparing grade table for teacher. Request: {}", request);

        TermPeriod termPeriod = termPeriodService.getById(request.getTermPeriodId());
        Subject selectedSubject = subjectService.findById(request.getSubjectId());
        log.debug("Found term period: {} and subject: {}", termPeriod, selectedSubject);

        List<LessonDateDTO> lessonDates = periodLessonService.findByDateForTeacher(request, termPeriod);
        List<Pupil> pupils = pupilService.findByClass(classesService.findById(request.getClassId()));
        log.debug("Found {} lesson dates and {} pupils", lessonDates.size(), pupils.size());

        List<PupilGradeDTO> pupilsGrades = pupils.stream()
                .map(pupil -> {
                    Map<Long, Integer> gradesMap = gradeRepository
                            .findByPupilAndSubjectAndPeriod(
                                    pupil,
                                    selectedSubject,
                                    termPeriod.getStartDate(),
                                    termPeriod.getEndDate())
                            .stream()
                            .collect(Collectors.toMap(
                                    grade -> grade.getPeriodLesson().getId(),
                                    Grade::getGrade
                            ));

                    log.trace("Processed grades for pupil {}", pupil.getId());
                    return new PupilGradeDTO(pupil, gradesMap);
                })
                .toList();

        List<Subject> availableSubjects = periodLessonService.findAvailableSubjects(
                request.getTeacherId(), request.getClassId());

        log.info("Successfully prepared grade table for {} pupils", pupilsGrades.size());
        return new GradeTableDTO(
                classesService.findById(request.getClassId()),
                teacherService.findById(request.getTeacherId()),
                termPeriod,
                selectedSubject,
                lessonDates,
                pupilsGrades,
                availableSubjects
        );
    }

    @Override
    @Transactional(readOnly = true)
    public PupilGradesOverviewDTO getPupilGradesOverview(GradeSetupPupilDTO request) {
        log.info("Getting grades overview for pupil: {}", request.getPupil().getId());

        Classes schoolClass = request.getPupil().getClasses();
        TermPeriod termPeriod = termPeriodService.getById(request.getTermPeriodId());
        log.debug("Processing class {} and term period {}", schoolClass.getId(), termPeriod.getId());

        List<LocalDate> dates = generateWorkingDates(termPeriod.getStartDate(), termPeriod.getEndDate());
        log.trace("Generated {} working dates", dates.size());

        PeriodSchedule periodSchedule = periodScheduleRepository.findByClassesAndPeriodWithLessons(schoolClass, termPeriod)
                .orElseThrow(() -> {
                    log.error("Schedule not found for class {} and period {}",
                            schoolClass.getId(), termPeriod.getId());
                    return new EntityNotFoundException("Schedule not found");
                });

        Map<Subject, List<PeriodLesson>> lessonsBySubject = periodSchedule.getLessons().stream()
                .collect(Collectors.groupingBy(PeriodLesson::getSubject));
        log.debug("Grouped lessons by {} subjects", lessonsBySubject.size());

        List<SubjectGradesDTO> subjectsGrades = lessonsBySubject.entrySet().stream()
                .map(entry -> processSubjectGrades(entry, request.getPupil(), termPeriod))
                .collect(Collectors.toList());

        log.info("Successfully retrieved grades overview for pupil {}", request.getPupil().getId());
        return new PupilGradesOverviewDTO(request.getPupil(), termPeriod, subjectsGrades, dates);
    }

    public List<LocalDate> generateWorkingDates(LocalDate startDate, LocalDate endDate) {
        List<LocalDate> dates = new ArrayList<>();
        LocalDate current = startDate;

        while (!current.isAfter(endDate)) {
            if (current.getDayOfWeek() != DayOfWeek.SUNDAY) {
                dates.add(current);
            }
            current = current.plusDays(1);
        }

        return dates;
    }

    public SubjectGradesDTO processSubjectGrades(
            Map.Entry<Subject, List<PeriodLesson>> entry,
            Pupil pupil,
            TermPeriod termPeriod) {

        Subject subject = entry.getKey();
        log.trace("Processing grades for subject {}", subject.getId());

        List<PeriodLesson> lessons = entry.getValue();

        Map<Long, Integer> gradesMap = gradeRepository
                .findByPupilAndSubjectAndPeriod(
                        pupil,
                        subject,
                        termPeriod.getStartDate(),
                        termPeriod.getEndDate())
                .stream()
                .collect(Collectors.toMap(
                        grade -> grade.getPeriodLesson().getId(),
                        Grade::getGrade
                ));

        Map<LocalDate, List<Integer>> gradesByDate = new HashMap<>();

        lessons.forEach(lesson -> {
            Integer grade = gradesMap.get(lesson.getId());
            if (grade != null) {
                gradesByDate.computeIfAbsent(lesson.getDate(), k -> new ArrayList<>()).add(grade);
            }
        });

        double avg = gradesByDate.values().stream()
                .flatMap(List::stream)
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0.0);

        return new SubjectGradesDTO(subject, gradesByDate, avg);
    }

    @Override
    @Transactional
    public void saveGradesBatch(GradeSaveRequest request) {
        log.info("Saving batch of grades for class {}, subject {}",
                request.getClassId(), request.getSubjectId());

        TermPeriod period = termPeriodService.getById(request.getTermPeriodId());
        log.debug("Processing term period: {}", period.getId());

        List<Grade> existingGrades = gradeRepository.findByClassAndSubjectAndPeriod(
                request.getClassId(),
                request.getSubjectId(),
                period.getStartDate(),
                period.getEndDate()
        );
        log.debug("Found {} existing grades", existingGrades.size());

        Map<String, Grade> existingGradesMap = existingGrades.stream()
                .collect(Collectors.toMap(
                        g -> g.getPupil().getId() + "_" + g.getPeriodLesson().getId(),
                        Function.identity()
                ));

        List<Grade> gradesToSave = new ArrayList<>();

        request.getGrades().forEach((pupilId, lessonGrades) -> {
            Pupil pupil = pupilService.findById(pupilId);
            log.trace("Processing grades for pupil {}", pupilId);

            lessonGrades.forEach((lessonId, gradeValue) -> {
                PeriodLesson lesson = periodLessonService.findByLessonId(lessonId);

                String key = pupilId + "_" + lessonId;
                if (existingGradesMap.containsKey(key)) {
                    Grade existing = existingGradesMap.get(key);
                    existing.setGrade(gradeValue);
                    gradesToSave.add(existing);
                    log.trace("Updated grade for pupil {}, lesson {}", pupilId, lessonId);
                } else {
                    Grade newGrade = new Grade();
                    newGrade.setPupil(pupil);
                    newGrade.setSubject(lesson.getSubject());
                    newGrade.setPeriodLesson(lesson);
                    newGrade.setGrade(gradeValue);
                    gradesToSave.add(newGrade);
                    log.trace("Created new grade for pupil {}, lesson {}", pupilId, lessonId);
                }
            });
        });

        gradeRepository.saveAll(gradesToSave);
        log.info("Successfully saved {} grades", gradesToSave.size());
    }

    @Override
    @Transactional(readOnly = true)
    public Map<Long, Grade> getGradesForPupilAndWeek(Pupil pupil, LocalDate startDate, LocalDate endDate) {
        log.info("Getting grades for pupil {} between {} and {}",
                pupil.getId(), startDate, endDate);

        Map<Long, Grade> grades = gradeRepository.findByPupilAndLessonDateBetween(pupil.getId(), startDate, endDate)
                .stream()
                .collect(Collectors.toMap(
                        grade -> grade.getPeriodLesson().getId(),
                        Function.identity()
                ));

        log.debug("Found {} grades for pupil {}", grades.size(), pupil.getId());
        return grades;
    }
}
