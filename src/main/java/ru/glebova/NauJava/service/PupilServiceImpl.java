package ru.glebova.NauJava.service;

import org.springframework.stereotype.Service;
import ru.glebova.NauJava.domain.Pupil;
import ru.glebova.NauJava.repository.PupilRepository;


@Service
public class PupilServiceImpl implements PupilService {

    private final PupilRepository pupilRepository;

    public PupilServiceImpl(PupilRepository pupilRepository) {
        this.pupilRepository = pupilRepository;
    }

    @Override
    public void createPupil(Long id, String firstname, String lastname) {
        if (id == null || firstname == null || lastname == null) {
            throw new IllegalArgumentException("ID, имя и фамилия ученика не могут быть null.");
        }
        if (firstname.isBlank() || lastname.isBlank()) {
            throw new IllegalArgumentException("Имя и фамилия ученика не могут быть пустыми.");
        }

        if (pupilRepository.read(id) != null) {
            throw new IllegalArgumentException("Ученик с id " + id + " уже существует.");
        }

        Pupil newPupil = new Pupil();
        newPupil.setId(id);
        newPupil.setFirstname(firstname);
        newPupil.setLastname(lastname);
        pupilRepository.create(newPupil);
    }

    @Override
    public Pupil findById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID ученика не может быть null.");
        }

        Pupil pupil = pupilRepository.read(id);
        if (pupil == null) {
            throw new IllegalArgumentException("Ученик с id " + id + " не найден.");
        }
        return pupil;
    }

    @Override
    public void deleteById(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID ученика не может быть null.");
        }

        if (pupilRepository.read(id) == null) {
            throw new IllegalArgumentException("Ученик с id " + id + " не найден.");
        }

        pupilRepository.delete(id);
    }

    @Override
    public void updateProfile(Long id, String newFirstname, String newLastname) {
        if (id == null || newFirstname == null || newLastname == null) {
            throw new IllegalArgumentException("ID, имя и фамилия ученика не могут быть null.");
        }

        Pupil pupil = pupilRepository.read(id);
        if (pupil == null) {
            throw new IllegalArgumentException("Ученик с id " + id + " не найден.");
        }

        pupil.setFirstname(newFirstname);
        pupil.setLastname(newLastname);
        pupilRepository.update(pupil);
    }
}