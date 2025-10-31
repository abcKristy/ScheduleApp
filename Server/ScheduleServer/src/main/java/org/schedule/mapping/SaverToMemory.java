package org.schedule.mapping;

import org.schedule.entity.forBD.LessonEntity;
import org.schedule.entity.forBD.GroupEntity;
import org.schedule.entity.forBD.TeacherEntity;
import org.schedule.repository.LessonRepository;
import org.schedule.repository.GroupRepository;
import org.schedule.repository.TeacherRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class SaverToMemory {
    private static final Logger log = LoggerFactory.getLogger(SaverToMemory.class);

    private final LessonRepository lessonRepository;
    private final GroupRepository groupRepository;
    private final TeacherRepository teacherRepository;

    public SaverToMemory(LessonRepository lessonRepository,
                         GroupRepository groupRepository,
                         TeacherRepository teacherRepository) {
        this.lessonRepository = lessonRepository;
        this.groupRepository = groupRepository;
        this.teacherRepository = teacherRepository;
    }

    /**
     * Сохраняет одно занятие в кэш (заглушка для будущей реализации)
     */
    public void saveToCache(LessonEntity schedule) {
        // TODO: Реализовать сохранение в кэш
        log.debug("Сохранение занятия в кэш: {} - {}",
                schedule.getDiscipline(), schedule.getStartTime());
    }

    /**
     * Сохраняет одно занятие в базу данных с обработкой связей
     */
    @Transactional
    public void saveToDatabase(LessonEntity lesson) {
        try {
            log.info("Сохранение занятия в БД: {} - {}",
                    lesson.getDiscipline(), lesson.getStartTime());

            // Обрабатываем группы (проверяем существование и сохраняем связи)
            processGroups(lesson);

            // Обрабатываем преподавателя (если есть)
            processTeacher(lesson);

            // Проверяем существование занятия по UID
            if (lesson.getUid() != null) {
                Optional<LessonEntity> existingLesson = lessonRepository.findByUid(lesson.getUid());
                if (existingLesson.isPresent()) {
                    log.debug("Занятие с UID {} уже существует, обновляем", lesson.getUid());
                    updateExistingLesson(existingLesson.get(), lesson);
                    return;
                }
            }

            // Сохраняем новое занятие
            lessonRepository.save(lesson);
            log.debug("Занятие успешно сохранено в БД");

        } catch (Exception e) {
            log.error("Ошибка при сохранении занятия в БД: {}", e.getMessage(), e);
            throw new RuntimeException("Не удалось сохранить занятие в БД", e);
        }
    }

    /**
     * Сохраняет список занятий в базу данных
     */
    @Transactional
    public void saveAllToDatabase(List<LessonEntity> lessons) {
        log.info("Сохранение {} занятий в БД", lessons.size());

        int savedCount = 0;
        int skippedCount = 0;

        for (LessonEntity lesson : lessons) {
            try {
                saveToDatabase(lesson);
                savedCount++;
            } catch (Exception e) {
                log.warn("Не удалось сохранить занятие: {} - {}",
                        lesson.getDiscipline(), lesson.getStartTime());
                skippedCount++;
            }
        }

        log.info("Сохранение завершено: успешно - {}, пропущено - {}",
                savedCount, skippedCount);
    }

    /**
     * Обрабатывает группы занятия: проверяет существование и устанавливает связи
     */
    private void processGroups(LessonEntity lesson) {
        if (lesson.getGroups() == null || lesson.getGroups().isEmpty()) {
            return;
        }

        List<GroupEntity> processedGroups = new ArrayList<>();

        for (GroupEntity group : lesson.getGroups()) {
            if (group.getGroupName() != null && !group.getGroupName().trim().isEmpty()) {
                // Ищем существующую группу или создаем новую
                GroupEntity existingGroup = groupRepository.findByGroupName(group.getGroupName())
                        .orElseGet(() -> {
                            GroupEntity newGroup = new GroupEntity();
                            newGroup.setGroupName(group.getGroupName());
                            return groupRepository.save(newGroup);
                        });
                processedGroups.add(existingGroup);
            }
        }

        lesson.setGroups(processedGroups);
    }

    /**
     * Обрабатывает преподавателя занятия
     */
    private void processTeacher(LessonEntity lesson) {
        String teacherName = lesson.getTeacher();
        if (teacherName == null || teacherName.trim().isEmpty()) {
            return;
        }

        // TODO: Реализовать логику связывания с сущностью TeacherEntity
        // Пока сохраняем только имя преподавателя как строку в LessonEntity
        log.debug("Преподаватель занятия: {}", teacherName);
    }

    /**
     * Обновляет существующее занятие новыми данными
     */
    /**
     * Обновляет существующее занятие новыми данными
     */
    private void updateExistingLesson(LessonEntity existing, LessonEntity newData) {
        existing.setDiscipline(newData.getDiscipline());
        existing.setLessonType(newData.getLessonType());
        existing.setStartTime(newData.getStartTime());
        existing.setEndTime(newData.getEndTime());
        existing.setRoom(newData.getRoom());
        existing.setTeacher(newData.getTeacher());
        // Удален setSummary, теперь используем groups
        existing.setRecurrence(newData.getRecurrence());
        existing.setExceptions(newData.getExceptions());

        // Обновляем группы
        existing.getGroups().clear();
        if (newData.getGroups() != null) {
            existing.getGroups().addAll(newData.getGroups());
        }

        lessonRepository.save(existing);
    }
}