package org.schedule.mapping;

import org.schedule.entity.apidata.ResponseDto;
import org.schedule.entity.forBD.basic.LessonEntity;
import org.schedule.entity.forBD.basic.GroupEntity;
import org.schedule.entity.forBD.basic.RoomEntity;
import org.schedule.entity.forBD.basic.TeacherEntity;
import org.schedule.repository.LessonRepository;
import org.schedule.repository.GroupRepository;
import org.schedule.repository.RoomRepository;
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
    private final RoomRepository roomRepository;

    public SaverToMemory(LessonRepository lessonRepository,
                         GroupRepository groupRepository,
                         TeacherRepository teacherRepository,
                         RoomRepository roomRepository) {
        this.lessonRepository = lessonRepository;
        this.groupRepository = groupRepository;
        this.teacherRepository = teacherRepository;
        this.roomRepository = roomRepository;
    }

    @Transactional
    public void saveToDatabase(LessonEntity lesson) {
        if (lesson == null) {
            throw new IllegalArgumentException("Занятие не может быть null");
        }
        try {
            // Сначала сохраняем связанные сущности
            processGroups(lesson);
            processTeachers(lesson);
            processRooms(lesson);

            // Теперь проверяем существование занятия
            if (lesson.getUid() != null) {
                Optional<LessonEntity> existingLesson = lessonRepository.findByUid(lesson.getUid());
                if (existingLesson.isPresent()) {
                    updateExistingLesson(existingLesson.get(), lesson);
                    return;
                }
            }

            // Сохраняем занятие
            lessonRepository.save(lesson);

        } catch (Exception e) {
            log.error("Ошибка при сохранении занятия в БД: {}", e.getMessage(), e);
            throw new RuntimeException("Не удалось сохранить занятие в БД", e);
        }
    }

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
                            // ЯВНО сохраняем новую группу перед использованием
                            return groupRepository.save(newGroup);
                        });
                processedGroups.add(existingGroup);
            }
        }

        lesson.setGroups(processedGroups);
    }

    private void processTeachers(LessonEntity lesson) {
        String teacherName = lesson.getTeacher();
        if (teacherName == null || teacherName.trim().isEmpty()) {
            return;
        }

        List<TeacherEntity> processedTeachers = new ArrayList<>();

        String[] teacherNames = teacherName.split("[,\n]");
        for (String name : teacherNames) {
            String cleanName = name.trim();
            if (!cleanName.isEmpty()) {
                TeacherEntity existingTeacher = teacherRepository.findByFullName(cleanName)
                        .orElseGet(() -> {
                            TeacherEntity newTeacher = new TeacherEntity();
                            newTeacher.setFullName(cleanName);
                            // ЯВНО сохраняем нового преподавателя
                            return teacherRepository.save(newTeacher);
                        });
                processedTeachers.add(existingTeacher);
            }
        }

        lesson.setTeachers(processedTeachers);
    }

    private void processRooms(LessonEntity lesson) {
        String roomName = lesson.getRoom();
        if (roomName == null || roomName.trim().isEmpty()) {
            return;
        }

        List<RoomEntity> processedRooms = new ArrayList<>();

        String[] roomNames = roomName.split(",");
        for (String name : roomNames) {
            String cleanName = name.trim();
            if (!cleanName.isEmpty()) {
                RoomEntity existingRoom = roomRepository.findByRoomName(cleanName)
                        .orElseGet(() -> {
                            RoomEntity newRoom = new RoomEntity();
                            newRoom.setRoomName(cleanName);
                            // ЯВНО сохраняем новую аудиторию
                            return roomRepository.save(newRoom);
                        });
                processedRooms.add(existingRoom);
            }
        }

        lesson.setRooms(processedRooms);
    }

    private void updateExistingLesson(LessonEntity existing, LessonEntity newData) {
        existing.setDiscipline(newData.getDiscipline());
        existing.setLessonType(newData.getLessonType());
        existing.setStartTime(newData.getStartTime());
        existing.setEndTime(newData.getEndTime());
        existing.setRoom(newData.getRoom());
        existing.setTeacher(newData.getTeacher());
        existing.setRecurrence(newData.getRecurrence());
        existing.setExceptions(newData.getExceptions());

        existing.getGroups().clear();
        if (newData.getGroups() != null) {
            existing.getGroups().addAll(newData.getGroups());
        }

        existing.getTeachers().clear();
        if (newData.getTeachers() != null) {
            existing.getTeachers().addAll(newData.getTeachers());
        }

        existing.getRooms().clear();
        if (newData.getRooms() != null) {
            existing.getRooms().addAll(newData.getRooms());
        }

        lessonRepository.save(existing);
    }

    @Transactional
    public void updateIdFromApi(ResponseDto responseDto) {
        try {
            Long apiId = responseDto.getId();
            String fullTitle = responseDto.getFullTitle();
            Integer target = responseDto.getTarget();

            if (apiId == null || fullTitle == null || target == null) {
                log.warn("Недостаточно данных для обновления id_from_api");
                return;
            }

            switch (target) {
                case 1:
                    updateGroupIdFromApi(fullTitle, apiId);
                    break;
                case 2:
                    updateTeacherIdFromApi(fullTitle, apiId);
                    break;
                case 3:
                    updateRoomIdFromApi(fullTitle, apiId);
                    break;
                default:
                    log.warn("Неизвестный target: {}", target);
            }

        } catch (Exception e) {
            log.error("Ошибка при обновлении id_from_api для ResponseDto {}: {}", responseDto.getId(), e.getMessage(), e);
        }
    }

    private void updateGroupIdFromApi(String groupName, Long apiId) {
        Optional<GroupEntity> groupOpt = groupRepository.findByGroupName(groupName);
        if (groupOpt.isPresent()) {
            GroupEntity group = groupOpt.get();
            group.setIdFromApi(apiId);
            groupRepository.save(group);
        } else {
            log.warn("Группа '{}' не найдена в БД для обновления id_from_api", groupName);
        }
    }

    private void updateTeacherIdFromApi(String teacherName, Long apiId) {
        Optional<TeacherEntity> teacherOpt = teacherRepository.findByFullName(teacherName);
        if (teacherOpt.isPresent()) {
            TeacherEntity teacher = teacherOpt.get();
            teacher.setIdFromApi(apiId);
            teacherRepository.save(teacher);
        } else {
            log.warn("Преподаватель '{}' не найден в БД для обновления id_from_api", teacherName);
        }
    }

    private void updateRoomIdFromApi(String roomName, Long apiId) {
        Optional<RoomEntity> roomOpt = roomRepository.findByRoomName(roomName);
        if (roomOpt.isPresent()) {
            RoomEntity room = roomOpt.get();
            room.setIdFromApi(apiId);
            roomRepository.save(room);
        } else {
            log.warn("Аудитория '{}' не найдена в БД для обновления id_from_api", roomName);
        }
    }

    @Transactional
    public void updateAllIdsFromApi(List<ResponseDto> responseDtos) {
        log.info("Вход в updateAllIdsFromApi, объектов: {}", responseDtos.size());

        int updatedCount = 0;
        int skippedCount = 0;

        for (ResponseDto dto : responseDtos) {
            try {
                updateIdFromApi(dto);
                updatedCount++;
            } catch (Exception e) {
                log.warn("Не удалось обновить id_from_api для ResponseDto {}", dto.getId());
                skippedCount++;
            }
        }

        log.info("Выход из updateAllIdsFromApi, результат: успешно - {}, пропущено - {}", updatedCount, skippedCount);
    }

    public void saveToCache(List<LessonEntity> lessons) {
        log.debug("Вход в saveToCache, занятий: {}", lessons.size());
        // TODO: Реализовать сохранение в saveToCache
        log.debug("Выход из saveToCache");
    }

    @Transactional
    public void saveLessonsWithErrorHandling(List<LessonEntity> lessons) {
        log.info("Пакетное сохранение занятий с обработкой ошибок, занятий: {}", lessons.size());

        if (lessons.isEmpty()) {
            log.warn("Пустой список занятий для сохранения");
            return;
        }

        int savedCount = 0;
        int errorCount = 0;
        List<String> errors = new ArrayList<>();

        for (int i = 0; i < lessons.size(); i++) {
            LessonEntity lesson = lessons.get(i);
            try {
                if (lesson == null) {
                    errors.add("Элемент " + i + ": null объект");
                    errorCount++;
                    continue;
                }

                saveToDatabase(lesson);
                savedCount++;

            } catch (Exception e) {
                errorCount++;
                String errorMsg = String.format("Занятие %d '%s': %s",
                        i,
                        lesson != null ? lesson.getDiscipline() : "null",
                        e.getMessage()
                );
                errors.add(errorMsg);
                log.warn("Не удалось сохранить занятие: {}", errorMsg);
            }
        }

        log.info("Пакетное сохранение завершено: успешно - {}, ошибок - {}", savedCount, errorCount);

        if (errorCount > 0) {
            // Логируем все ошибки для отладки
            errors.forEach(error -> log.debug("Ошибка сохранения: {}", error));

            // Бросаем исключение только если не сохранено ни одного занятия
            if (savedCount == 0) {
                throw new RuntimeException("Не удалось сохранить ни одного занятия: " +
                        String.join("; ", errors.subList(0, Math.min(5, errors.size()))));
            }

            // Или если ошибок слишком много (например, больше 50%)
            if (errorCount > lessons.size() / 2) {
                throw new RuntimeException("Слишком много ошибок при сохранении: " +
                        savedCount + "/" + lessons.size() + " успешно. Ошибки: " +
                        String.join("; ", errors.subList(0, Math.min(3, errors.size()))));
            }
        }
    }
}