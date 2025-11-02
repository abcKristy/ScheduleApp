package org.schedule.mapping;

import org.schedule.entity.apidata.ResponseDto;
import org.schedule.entity.forBD.basic.LessonEntity;
import org.schedule.entity.forBD.basic.GroupEntity;
import org.schedule.entity.forBD.basic.RoomEntity;
import org.schedule.entity.forBD.basic.TeacherEntity;
import org.schedule.entity.schedule.ScheduleDto;
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

    public void saveToCache(LessonEntity schedule) {
        // TODO: Реализовать сохранение в кэш
        log.debug("Сохранение занятия в кэш: {} - {}",
                schedule.getDiscipline(), schedule.getStartTime());
    }
    @Transactional
    public void saveToDatabase(LessonEntity lesson) {
        try {
            log.info("Сохранение занятия в БД: {} - {}",
                    lesson.getDiscipline(), lesson.getStartTime());

            // Обрабатываем группы
            processGroups(lesson);

            // Обрабатываем преподавателей
            processTeachers(lesson);

            // Обрабатываем аудитории
            processRooms(lesson);

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
    private void processTeachers(LessonEntity lesson) {
        String teacherName = lesson.getTeacher();
        if (teacherName == null || teacherName.trim().isEmpty()) {
            return;
        }

        List<TeacherEntity> processedTeachers = new ArrayList<>();

        // Разделяем преподавателей, если их несколько (через запятую или \n)
        String[] teacherNames = teacherName.split("[,\n]");
        for (String name : teacherNames) {
            String cleanName = name.trim();
            if (!cleanName.isEmpty()) {
                TeacherEntity existingTeacher = teacherRepository.findByFullName(cleanName)
                        .orElseGet(() -> {
                            TeacherEntity newTeacher = new TeacherEntity();
                            newTeacher.setFullName(cleanName);
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

        // Разделяем аудитории, если их несколько (через запятую)
        String[] roomNames = roomName.split(",");
        for (String name : roomNames) {
            String cleanName = name.trim();
            if (!cleanName.isEmpty()) {
                RoomEntity existingRoom = roomRepository.findByRoomName(cleanName)
                        .orElseGet(() -> {
                            RoomEntity newRoom = new RoomEntity();
                            newRoom.setRoomName(cleanName);
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

        // Обновляем группы
        existing.getGroups().clear();
        if (newData.getGroups() != null) {
            existing.getGroups().addAll(newData.getGroups());
        }

        // Обновляем преподавателей
        existing.getTeachers().clear();
        if (newData.getTeachers() != null) {
            existing.getTeachers().addAll(newData.getTeachers());
        }

        // Обновляем аудитории
        existing.getRooms().clear();
        if (newData.getRooms() != null) {
            existing.getRooms().addAll(newData.getRooms());
        }

        lessonRepository.save(existing);
    }

    @Transactional
    public void updateIdFromApi(ResponseDto responseDto) {
        try {
            log.info("Обновление id_from_api для ResponseDto: id={}, target={}, fullTitle='{}'",
                    responseDto.getId(), responseDto.getTarget(), responseDto.getFullTitle());

            Long apiId = responseDto.getId();
            String fullTitle = responseDto.getFullTitle();
            Integer target = responseDto.getTarget();

            if (apiId == null || fullTitle == null || target == null) {
                log.warn("Недостаточно данных для обновления id_from_api");
                return;
            }

            switch (target) {
                case 1: // Группы
                    updateGroupIdFromApi(fullTitle, apiId);
                    break;
                case 2: // Преподаватели
                    updateTeacherIdFromApi(fullTitle, apiId);
                    break;
                case 3: // Аудитории
                    updateRoomIdFromApi(fullTitle, apiId);
                    break;
                default:
                    log.warn("Неизвестный target: {}", target);
            }

            log.info("Успешно обновлен id_from_api для target={}, fullTitle='{}'", target, fullTitle);

        } catch (Exception e) {
            log.error("Ошибка при обновлении id_from_api для ResponseDto {}: {}",
                    responseDto.getId(), e.getMessage(), e);
        }
    }

    private void updateGroupIdFromApi(String groupName, Long apiId) {
        Optional<GroupEntity> groupOpt = groupRepository.findByGroupName(groupName);
        if (groupOpt.isPresent()) {
            GroupEntity group = groupOpt.get();
            group.setIdFromApi(apiId);
            groupRepository.save(group);
            log.debug("Обновлен id_from_api для группы '{}': {}", groupName, apiId);
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
            log.debug("Обновлен id_from_api для преподавателя '{}': {}", teacherName, apiId);
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
            log.debug("Обновлен id_from_api для аудитории '{}': {}", roomName, apiId);
        } else {
            log.warn("Аудитория '{}' не найдена в БД для обновления id_from_api", roomName);
        }
    }

    @Transactional
    public void updateAllIdsFromApi(List<ResponseDto> responseDtos) {
        log.info("Обновление id_from_api для {} ResponseDto объектов", responseDtos.size());

        int updatedCount = 0;
        int skippedCount = 0;

        for (ResponseDto dto : responseDtos) {
            try {
                updateIdFromApi(dto);
                updatedCount++;
            } catch (Exception e) {
                log.warn("Не удалось обновить id_from_api для ResponseDto {}: {}",
                        dto.getId(), e.getMessage());
                skippedCount++;
            }
        }

        log.info("Обновление id_from_api завершено: успешно - {}, пропущено - {}",
                updatedCount, skippedCount);
    }

    @Transactional
    public void saveLessons(List<LessonEntity> lessons) {
        log.info("Сохранение {} занятий", lessons.size());

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

        log.info("Сохранение завершено: успешно - {}, пропущено - {}", savedCount, skippedCount);
    }

    public void saveToCache(List<LessonEntity> lessons) {
        log.debug("Сохранение {} занятий в кэш", lessons.size());
        // TODO: Реализовать сохранение в Redis/Memcached
        for (LessonEntity lesson : lessons) {
            log.debug("Сохранение в кэш: {} - {}", lesson.getDiscipline(), lesson.getStartTime());
        }
    }
}