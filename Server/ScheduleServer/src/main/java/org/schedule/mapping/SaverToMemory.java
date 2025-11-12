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

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    /**
     * ПОЛНОЕ ПАКЕТНОЕ СОХРАНЕНИЕ УРОКОВ
     */
    @Transactional
    public BatchSaveResult saveLessonsBatch(List<LessonEntity> lessons) {
        log.info("Начало пакетного сохранения {} уроков", lessons.size());

        if (lessons == null || lessons.isEmpty()) {
            log.warn("Пустой список уроков для пакетного сохранения");
            return new BatchSaveResult(0, 0, 0, Collections.emptyList());
        }

        long startTime = System.currentTimeMillis();

        try {
            // 1. ПОДГОТОВКА ВСЕХ СПРАВОЧНИКОВ
            log.debug("Этап 1: Подготовка справочников");
            ReferenceData referenceData = prepareAllReferences(lessons);

            // 2. ОБРАБОТКА И СВЯЗЫВАНИЕ УРОКОВ
            log.debug("Этап 2: Обработка уроков");
            ProcessedLessons processedLessons = processAndLinkLessons(lessons, referenceData);

            // 3. ПАКЕТНОЕ СОХРАНЕНИЕ
            log.debug("Этап 3: Пакетное сохранение");
            return saveAllInBatch(processedLessons, startTime);

        } catch (Exception e) {
            log.error("Критическая ошибка при пакетном сохранении: {}", e.getMessage(), e);
            throw new RuntimeException("Не удалось выполнить пакетное сохранение уроков", e);
        }
    }

    /**
     * 1. ПОДГОТОВКА ВСЕХ СПРАВОЧНИКОВ - ГРУППЫ, ПРЕПОДАВАТЕЛИ, АУДИТОРИИ
     */
    private ReferenceData prepareAllReferences(List<LessonEntity> lessons) {
        ReferenceData data = new ReferenceData();

        // Извлекаем все уникальные имена из всех уроков
        extractAllUniqueNames(lessons, data);

        // Загружаем существующие и создаем новые сущности
        loadAndCreateAllReferences(data);

        return data;
    }

    /**
     * Извлечение всех уникальных имен из уроков
     */
    private void extractAllUniqueNames(List<LessonEntity> lessons, ReferenceData data) {
        for (LessonEntity lesson : lessons) {
            if (lesson == null) continue;

            // Группы
            if (lesson.getGroups() != null) {
                lesson.getGroups().stream()
                        .filter(group -> group != null && group.getGroupName() != null)
                        .map(GroupEntity::getGroupName)
                        .map(String::trim)
                        .filter(name -> !name.isEmpty())
                        .forEach(data.allGroupNames::add);
            }

            // Преподаватели
            if (lesson.getTeacher() != null) {
                Arrays.stream(lesson.getTeacher().split("[,\n]"))
                        .map(String::trim)
                        .filter(name -> !name.isEmpty())
                        .forEach(data.allTeacherNames::add);
            }

            // Аудитории
            if (lesson.getRoom() != null) {
                Arrays.stream(lesson.getRoom().split(","))
                        .map(String::trim)
                        .filter(name -> !name.isEmpty())
                        .forEach(data.allRoomNames::add);
            }
        }

        log.debug("Извлечено уникальных имен: групп={}, преподавателей={}, аудиторий={}",
                data.allGroupNames.size(), data.allTeacherNames.size(), data.allRoomNames.size());
    }

    /**
     * Загрузка существующих и создание новых справочников
     */
    private void loadAndCreateAllReferences(ReferenceData data) {
        // ГРУППЫ
        if (!data.allGroupNames.isEmpty()) {
            // Загружаем существующие группы
            List<GroupEntity> existingGroups = groupRepository.findByGroupNameIn(
                    new ArrayList<>(data.allGroupNames));
            data.existingGroups = existingGroups.stream()
                    .collect(Collectors.toMap(GroupEntity::getGroupName, Function.identity()));

            // Создаем новые группы
            List<GroupEntity> newGroups = data.allGroupNames.stream()
                    .filter(name -> !data.existingGroups.containsKey(name))
                    .map(name -> {
                        GroupEntity group = new GroupEntity();
                        group.setGroupName(name);
                        return group;
                    })
                    .collect(Collectors.toList());

            // Сохраняем новые группы ПАКЕТОМ
            if (!newGroups.isEmpty()) {
                List<GroupEntity> savedGroups = groupRepository.saveAll(newGroups);
                savedGroups.forEach(group -> data.existingGroups.put(group.getGroupName(), group));
                log.debug("Создано новых групп: {}", newGroups.size());
            }
        }

        // ПРЕПОДАВАТЕЛИ
        if (!data.allTeacherNames.isEmpty()) {
            List<TeacherEntity> existingTeachers = teacherRepository.findByFullNamesIn(
                    new ArrayList<>(data.allTeacherNames));
            data.existingTeachers = existingTeachers.stream()
                    .collect(Collectors.toMap(TeacherEntity::getFullName, Function.identity()));

            List<TeacherEntity> newTeachers = data.allTeacherNames.stream()
                    .filter(name -> !data.existingTeachers.containsKey(name))
                    .map(name -> {
                        TeacherEntity teacher = new TeacherEntity();
                        teacher.setFullName(name);
                        return teacher;
                    })
                    .collect(Collectors.toList());

            if (!newTeachers.isEmpty()) {
                List<TeacherEntity> savedTeachers = teacherRepository.saveAll(newTeachers);
                savedTeachers.forEach(teacher -> data.existingTeachers.put(teacher.getFullName(), teacher));
                log.debug("Создано новых преподавателей: {}", newTeachers.size());
            }
        }

        // АУДИТОРИИ
        if (!data.allRoomNames.isEmpty()) {
            List<RoomEntity> existingRooms = roomRepository.findByRoomNamesIn(
                    new ArrayList<>(data.allRoomNames));
            data.existingRooms = existingRooms.stream()
                    .collect(Collectors.toMap(RoomEntity::getRoomName, Function.identity()));

            List<RoomEntity> newRooms = data.allRoomNames.stream()
                    .filter(name -> !data.existingRooms.containsKey(name))
                    .map(name -> {
                        RoomEntity room = new RoomEntity();
                        room.setRoomName(name);
                        return room;
                    })
                    .collect(Collectors.toList());

            if (!newRooms.isEmpty()) {
                List<RoomEntity> savedRooms = roomRepository.saveAll(newRooms);
                savedRooms.forEach(room -> data.existingRooms.put(room.getRoomName(), room));
                log.debug("Создано новых аудиторий: {}", newRooms.size());
            }
        }
    }

    /**
     * 2. ОБРАБОТКА И СВЯЗЫВАНИЕ УРОКОВ СО СПРАВОЧНИКАМИ
     */
    private ProcessedLessons processAndLinkLessons(List<LessonEntity> lessons, ReferenceData referenceData) {
        ProcessedLessons result = new ProcessedLessons();

        for (LessonEntity lesson : lessons) {
            if (lesson == null) {
                result.skippedLessons.add("null объект урока");
                continue;
            }

            try {
                // Обрабатываем группы
                if (lesson.getGroups() != null) {
                    List<GroupEntity> processedGroups = lesson.getGroups().stream()
                            .filter(group -> group != null && group.getGroupName() != null)
                            .map(group -> referenceData.existingGroups.get(group.getGroupName().trim()))
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList());
                    lesson.setGroups(processedGroups);
                }

                // Обрабатываем преподавателей
                if (lesson.getTeacher() != null) {
                    List<TeacherEntity> processedTeachers = Arrays.stream(lesson.getTeacher().split("[,\n]"))
                            .map(String::trim)
                            .filter(name -> !name.isEmpty())
                            .map(referenceData.existingTeachers::get)
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList());
                    lesson.setTeachers(processedTeachers);
                }

                // Обрабатываем аудитории
                if (lesson.getRoom() != null) {
                    List<RoomEntity> processedRooms = Arrays.stream(lesson.getRoom().split(","))
                            .map(String::trim)
                            .filter(name -> !name.isEmpty())
                            .map(referenceData.existingRooms::get)
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList());
                    lesson.setRooms(processedRooms);
                }

                result.validLessons.add(lesson);

            } catch (Exception e) {
                String errorMsg = String.format("Урок '%s': %s",
                        lesson.getDiscipline() != null ? lesson.getDiscipline() : "без названия",
                        e.getMessage());
                result.skippedLessons.add(errorMsg);
                log.warn("Не удалось обработать урок: {}", errorMsg);
            }
        }

        return result;
    }

    /**
     * 3. ФИНАЛЬНОЕ ПАКЕТНОЕ СОХРАНЕНИЕ
     */
    private BatchSaveResult saveAllInBatch(ProcessedLessons processedLessons, long startTime) {
        int totalLessons = processedLessons.validLessons.size() + processedLessons.skippedLessons.size();
        int savedCount = 0;
        int errorCount = processedLessons.skippedLessons.size();

        if (!processedLessons.validLessons.isEmpty()) {
            try {
                // ЕДИНСТВЕННЫЙ вызов saveAll для всех уроков
                List<LessonEntity> savedLessons = lessonRepository.saveAll(processedLessons.validLessons);
                savedCount = savedLessons.size();

            } catch (Exception e) {
                log.error("Ошибка при пакетном сохранении уроков: {}", e.getMessage(), e);
                errorCount += processedLessons.validLessons.size();
                processedLessons.skippedLessons.add("Пакетное сохранение: " + e.getMessage());
            }
        }

        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        log.info("Пакетное сохранение завершено за {} мс: успешно={}, ошибок={}, всего={}",
                duration, savedCount, errorCount, totalLessons);

        // Логируем ошибки если есть
        if (!processedLessons.skippedLessons.isEmpty()) {
            log.warn("Пропущенные уроки ({}/{}):", errorCount, totalLessons);
            processedLessons.skippedLessons.forEach(error -> log.debug(" - {}", error));
        }

        return new BatchSaveResult(savedCount, errorCount, totalLessons, processedLessons.skippedLessons);
    }

    // ВСПОМОГАТЕЛЬНЫЕ КЛАССЫ ДЛЯ ОРГАНИЗАЦИИ ДАННЫХ

    /**
     * Данные всех справочников
     */
    private static class ReferenceData {
        Set<String> allGroupNames = new HashSet<>();
        Set<String> allTeacherNames = new HashSet<>();
        Set<String> allRoomNames = new HashSet<>();

        Map<String, GroupEntity> existingGroups = new HashMap<>();
        Map<String, TeacherEntity> existingTeachers = new HashMap<>();
        Map<String, RoomEntity> existingRooms = new HashMap<>();
    }

    /**
     * Обработанные уроки
     */
    private static class ProcessedLessons {
        List<LessonEntity> validLessons = new ArrayList<>();
        List<String> skippedLessons = new ArrayList<>();
    }

    /**
     * Результат пакетного сохранения
     */
    public static class BatchSaveResult {
        private final int savedCount;
        private final int errorCount;
        private final int totalCount;
        private final List<String> errors;

        public BatchSaveResult(int savedCount, int errorCount, int totalCount, List<String> errors) {
            this.savedCount = savedCount;
            this.errorCount = errorCount;
            this.totalCount = totalCount;
            this.errors = errors != null ? new ArrayList<>(errors) : new ArrayList<>();
        }

        // Геттеры
        public int getSavedCount() { return savedCount; }
        public int getErrorCount() { return errorCount; }
        public int getTotalCount() { return totalCount; }
        public List<String> getErrors() { return new ArrayList<>(errors); }
        public boolean isSuccess() { return errorCount == 0; }
        public double getSuccessRate() {
            return totalCount > 0 ? (double) savedCount / totalCount * 100 : 0;
        }
    }

    // СУЩЕСТВУЮЩИЕ МЕТОДЫ (оставляем для обратной совместимости)

    @Transactional
    public void saveToDatabase(LessonEntity lesson) {
        // Для обратной совместимости - используем пакетный метод для одного урока
        BatchSaveResult result = saveLessonsBatch(Collections.singletonList(lesson));
        if (result.getErrorCount() > 0) {
            throw new RuntimeException("Не удалось сохранить урок: " + String.join(", ", result.getErrors()));
        }
    }

    @Transactional
    public void saveLessonsWithErrorHandling(List<LessonEntity> lessons) {
        // Заменяем старую реализацию на новую пакетную
        BatchSaveResult result = saveLessonsBatch(lessons);

        if (result.getErrorCount() > 0) {
            // Сохраняем логику обработки ошибок из старого метода
            if (result.getSavedCount() == 0) {
                throw new RuntimeException("Не удалось сохранить ни одного занятия: " +
                        String.join("; ", result.getErrors().subList(0, Math.min(5, result.getErrors().size()))));
            }

            if (result.getErrorCount() > lessons.size() / 2) {
                throw new RuntimeException("Слишком много ошибок при сохранении: " +
                        result.getSavedCount() + "/" + lessons.size() + " успешно. Ошибки: " +
                        String.join("; ", result.getErrors().subList(0, Math.min(3, result.getErrors().size()))));
            }
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
}