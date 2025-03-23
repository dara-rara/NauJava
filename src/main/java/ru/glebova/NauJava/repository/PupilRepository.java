package ru.glebova.NauJava.repository;

import org.springframework.stereotype.Component;
import ru.glebova.NauJava.domain.Pupil;

import java.util.Map;

@Component
public class PupilRepository implements CrudRepository<Pupil, Long> {

    private final Map<Long, Pupil> pupilContainer;

    public PupilRepository(Map<Long, Pupil> pupilContainer) {
        this.pupilContainer = pupilContainer;
    }

    @Override
    public void create(Pupil pupil) {
        if (pupil == null) {
            throw new IllegalArgumentException("Ученик не может быть null.");
        }
        pupilContainer.put(pupil.getId(), pupil);
    }

    @Override
    public Pupil read(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID ученика не может быть null.");
        }
        return pupilContainer.get(id);
    }

    @Override
    public void update(Pupil pupil) {
        if (pupil == null) {
            throw new IllegalArgumentException("Ученик не может быть null.");
        }
        if (pupilContainer.replace(pupil.getId(), pupil) == null) {
            throw new IllegalArgumentException("Ученик с id " + pupil.getId() + " не найден.");
        }
    }

    @Override
    public void delete(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("ID ученика не может быть null.");
        }
        if (pupilContainer.remove(id) == null) {
            throw new IllegalArgumentException("Ученик с id " + id + " не найден.");
        }
    }
}