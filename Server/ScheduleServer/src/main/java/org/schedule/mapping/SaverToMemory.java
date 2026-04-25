package org.schedule.mapping;

import org.schedule.entity.apidata.ResponseDto;
import org.schedule.entity.forBD.ScheduleMetadataEntity;
import org.schedule.entity.forBD.basic.LessonEntity;
import org.schedule.entity.forBD.basic.GroupEntity;
import org.schedule.entity.forBD.basic.RoomEntity;
import org.schedule.entity.forBD.basic.TeacherEntity;
import org.schedule.repository.LessonRepository;
import org.schedule.repository.GroupRepository;
import org.schedule.repository.RoomRepository;
import org.schedule.repository.TeacherRepository;
import org.schedule.repository.ScheduleMetadataRepository;
import org.schedule.util.SemesterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class SaverToMemory {
    private static final Logger log = LoggerFactory.getLogger(SaverToMemory.class);

    private final LessonRepository lessonRepository;
    private final GroupRepository groupRepository;
    private final TeacherRepository teacherRepository;
    private final RoomRepository roomRepository;
    private final ScheduleMetadataRepository metadataRepository;

    public SaverToMemory(LessonRepository lessonRepository,
                         GroupRepository groupRepository,
                         TeacherRepository teacherRepository,
                         RoomRepository roomRepository,
                         ScheduleMetadataRepository metadataRepository) {
        this.lessonRepository = lessonRepository;
        this.groupRepository = groupRepository;
        this.teacherRepository = teacherRepository;
        this.roomRepository = roomRepository;
        this.metadataRepository = metadataRepository;
    }

    /**
     * ПАКЕТНОЕ СОХРАНЕНИЕ УРОКОВ С УЧЕТОМ СЕМЕСТРА
     */
    @Transactional
    public BatchSaveResult saveLessonsBatch(List<LessonEntity> lessons, String entityType, String entityName) {
        log.info("Начало пакетного сохранения {} уроков для {} {}", lessons.size(), entityType, entityName);

        if (lessons == null || lessons.isEmpty()) {
            log.warn("Пустой список уроков для пакетного сохранения");
            return new BatchSaveResult(0, 0, 0, Collections.emptyList());
        }

        long startTime = System.currentTimeMillis();

        try {
            String currentSemester = SemesterUtils.getCurrentSemester();
            log.debug("Текущий семестр: {}", currentSemester);

            lessons.forEach(lesson -> lesson.setSemester(currentSemester));

            log.debug("Этап 0: Усиленная дедупликация занятий");
            List<LessonEntity> deduplicatedLessons = deduplicateLessonsWithDBCheck(lessons);
            log.info("После дедупликации: {} -> {} занятий", lessons.size(), deduplicatedLessons.size());

            log.debug("Этап 1: Пакетная обработка справочников");
            if (shouldUseParallelProcessing(deduplicatedLessons)) {
                log.debug("Используется параллельная обработка");
                processAllReferencesParallel(deduplicatedLessons);
            } else {
                log.debug("Используется последовательная обработка");
                processAllReferencesBatch(deduplicatedLessons);
            }

            log.debug("Этап 2: Пакетное сохранение дедуплицированных уроков");
            List<LessonEntity> savedLessons = lessonRepository.saveAll(deduplicatedLessons);

            updateScheduleMetadata(entityType, entityName, currentSemester, savedLessons.size());

            long duration = System.currentTimeMillis() - startTime;
            log.info("Пакетное сохранение завершено за {} мс: успешно={} (дедуплицировано с {})",
                    duration, savedLessons.size(), lessons.size());

            return new BatchSaveResult(savedLessons.size(), 0, lessons.size(), Collections.emptyList());

        } catch (Exception e) {
            log.error("Критическая ошибка при пакетном сохранении: {}", e.getMessage(), e);
            throw new RuntimeException("Не удалось выполнить пакетное сохранение уроков", e);
        }
    }

    /**
     * Обновляет или создает метаданные о расписании
     */
    private void updateScheduleMetadata(String entityType, String entityName,
                                        String semester, int lessonCount) {
        try {
            Optional<ScheduleMetadataEntity> existingMetadata =
                    metadataRepository.findByEntityTypeAndEntityNameAndSemester(
                            entityType, entityName, semester);

            ScheduleMetadataEntity metadata;
            if (existingMetadata.isPresent()) {
                metadata = existingMetadata.get();
                metadata.setLastUpdated(LocalDateTime.now());
                metadata.setLessonCount(lessonCount);
            } else {
                metadata = new ScheduleMetadataEntity(entityType, entityName, semester);
                metadata.setLastUpdated(LocalDateTime.now());
                metadata.setLessonCount(lessonCount);
            }

            metadataRepository.save(metadata);
            log.debug("Метаданные обновлены: {} {} -> {} занятий, семестр {}",
                    entityType, entityName, lessonCount, semester);

        } catch (Exception e) {
            log.error("Ошибка при обновлении метаданных для {} {}", entityType, entityName, e);
        }
    }

    /**
     * УСИЛЕННАЯ ДЕДУПЛИКАЦИЯ С ПРОВЕРКОЙ БД
     */
    private List<LessonEntity> deduplicateLessonsWithDBCheck(List<LessonEntity> lessons) {
        log.info("Начало усиленной дедупликации {} занятий", lessons.size());

        List<LessonEntity> result = new ArrayList<>();
        Set<String> seenKeysInCurrentBatch = new HashSet<>();
        int duplicatesInBatch = 0;
        int duplicatesInDB = 0;

        for (LessonEntity lesson : lessons) {
            if (lesson == null) continue;

            String key = createDeduplicationKey(lesson);

            if (seenKeysInCurrentBatch.contains(key)) {
                log.debug("Пропуск дубликата в текущей пачке: {}", key);
                duplicatesInBatch++;
                continue;
            }

            boolean existsInDB = checkIfLessonExistsInDatabase(lesson);
            if (existsInDB) {
                log.debug("Пропуск дубликата (уже есть в БД): {}", key);
                duplicatesInDB++;
                continue;
            }

            result.add(lesson);
            seenKeysInCurrentBatch.add(key);
        }

        log.info("Дедупликация завершена. Оригиналов: {}, дубликатов в пачке: {}, дубликатов в БД: {}",
                result.size(), duplicatesInBatch, duplicatesInDB);

        return result;
    }

    private String createDeduplicationKey(LessonEntity lesson) {
        return String.format("%s|%s|%s|%s|%s|%s",
                lesson.getDiscipline() != null ? lesson.getDiscipline().trim().toLowerCase() : "",
                lesson.getLessonType() != null ? lesson.getLessonType().name() : "",
                lesson.getStartTime() != null ? lesson.getStartTime().toString() : "",
                lesson.getEndTime() != null ? lesson.getEndTime().toString() : "",
                normalizeGroups(lesson.getGroupsSummary()),
                lesson.getTeacher() != null ? lesson.getTeacher().trim().toLowerCase() : "");
    }

    private String normalizeGroups(String groupsSummary) {
        if (groupsSummary == null || groupsSummary.trim().isEmpty()) {
            return "";
        }

        return Arrays.stream(groupsSummary.split(",\\s*"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .sorted()
                .collect(Collectors.joining(", "))
                .toLowerCase();
    }

    private boolean checkIfLessonExistsInDatabase(LessonEntity lesson) {
        try {
            String currentSemester = SemesterUtils.getCurrentSemester();
            List<LessonEntity> existingLessons = lessonRepository
                    .findByDisciplineAndStartTimeAndEndTimeAndSemester(
                            lesson.getDiscipline(),
                            lesson.getStartTime(),
                            lesson.getEndTime(),
                            currentSemester
                    );

            for (LessonEntity existing : existingLessons) {
                if (isSameLesson(existing, lesson)) {
                    log.debug("Найден дубликат в БД: ID={}, {}", existing.getId(), createDeduplicationKey(lesson));
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            log.warn("Ошибка при проверке существования занятия в БД: {}", e.getMessage());
            return false;
        }
    }

    private boolean isSameLesson(LessonEntity lesson1, LessonEntity lesson2) {
        return Objects.equals(lesson1.getDiscipline(), lesson2.getDiscipline()) &&
                Objects.equals(lesson1.getLessonType(), lesson2.getLessonType()) &&
                Objects.equals(lesson1.getStartTime(), lesson2.getStartTime()) &&
                Objects.equals(lesson1.getEndTime(), lesson2.getEndTime()) &&
                Objects.equals(normalizeGroups(lesson1.getGroupsSummary()), normalizeGroups(lesson2.getGroupsSummary())) &&
                Objects.equals(
                        lesson1.getTeacher() != null ? lesson1.getTeacher().trim().toLowerCase() : "",
                        lesson2.getTeacher() != null ? lesson2.getTeacher().trim().toLowerCase() : ""
                );
    }

    private boolean shouldUseParallelProcessing(List<LessonEntity> lessons) {
        int threshold = 100;

        if (lessons.size() < threshold) {
            return false;
        }

        long totalReferences = lessons.stream()
                .filter(Objects::nonNull)
                .mapToLong(lesson -> {
                    long count = 0;
                    if (lesson.getGroups() != null) count += lesson.getGroups().size();
                    if (lesson.getTeacher() != null) count += lesson.getTeacher().split("[,\n]").length;
                    if (lesson.getRoom() != null) count += lesson.getRoom().split(",").length;
                    return count;
                })
                .sum();

        return totalReferences > 500;
    }

    private void processAllReferencesBatch(List<LessonEntity> lessons) {
        log.debug("Пакетная обработка справочников для {} уроков", lessons.size());

        processGroupsBatch(lessons);
        processTeachersBatch(lessons);
        processRoomsBatch(lessons);
    }

    private void processAllReferencesParallel(List<LessonEntity> lessons) {
        log.debug("Параллельная обработка справочников для {} уроков", lessons.size());

        CompletableFuture<Void> groupsFuture = CompletableFuture
                .runAsync(() -> processGroupsBatch(lessons))
                .exceptionally(ex -> {
                    log.error("Ошибка при параллельной обработке групп: {}", ex.getMessage());
                    return null;
                });

        CompletableFuture<Void> teachersFuture = CompletableFuture
                .runAsync(() -> processTeachersBatch(lessons))
                .exceptionally(ex -> {
                    log.error("Ошибка при параллельной обработке преподавателей: {}", ex.getMessage());
                    return null;
                });

        CompletableFuture<Void> roomsFuture = CompletableFuture
                .runAsync(() -> processRoomsBatch(lessons))
                .exceptionally(ex -> {
                    log.error("Ошибка при параллельной обработке аудиторий: {}", ex.getMessage());
                    return null;
                });

        try {
            CompletableFuture.allOf(groupsFuture, teachersFuture, roomsFuture).join();
            log.debug("Параллельная обработка справочников завершена");
        } catch (Exception e) {
            log.error("Критическая ошибка при параллельной обработке: {}", e.getMessage());
            throw new RuntimeException("Не удалось выполнить параллельную обработку", e);
        }
    }

    private void processGroupsBatch(List<LessonEntity> lessons) {
        Set<String> allGroupNames = lessons.stream()
                .filter(lesson -> lesson.getGroups() != null)
                .flatMap(lesson -> lesson.getGroups().stream())
                .map(GroupEntity::getGroupName)
                .filter(name -> name != null && !name.trim().isEmpty())
                .collect(Collectors.toSet());

        log.debug("Извлечено групп из занятий: {}", allGroupNames);

        if (allGroupNames.isEmpty()) {
            log.debug("Нет групп для обработки");
            return;
        }

        log.debug("Обработка {} уникальных групп", allGroupNames.size());

        Map<String, GroupEntity> existingGroupsMap = groupRepository
                .findByGroupNameIn(new ArrayList<>(allGroupNames))
                .stream()
                .collect(Collectors.toMap(GroupEntity::getGroupName, Function.identity()));

        List<GroupEntity> groupsToCreate = allGroupNames.stream()
                .filter(groupName -> !existingGroupsMap.containsKey(groupName))
                .map(groupName -> {
                    GroupEntity newGroup = new GroupEntity();
                    newGroup.setGroupName(groupName);
                    return newGroup;
                })
                .collect(Collectors.toList());

        if (!groupsToCreate.isEmpty()) {
            log.debug("Создание {} новых групп", groupsToCreate.size());
            List<GroupEntity> savedGroups = groupRepository.saveAll(groupsToCreate);
            savedGroups.forEach(group -> existingGroupsMap.put(group.getGroupName(), group));
        }

        for (LessonEntity lesson : lessons) {
            if (lesson.getGroups() != null) {
                List<GroupEntity> processedGroups = lesson.getGroups().stream()
                        .map(group -> existingGroupsMap.get(group.getGroupName()))
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
                lesson.setGroups(processedGroups);
            }
        }
    }

    private void processTeachersBatch(List<LessonEntity> lessons) {
        // Шаблон для ФИО: "Фамилия Имя Отчество" (все с большой буквы)
        java.util.regex.Pattern teacherNamePattern = java.util.regex.Pattern.compile(
                "[А-ЯЁ][а-яё]+\\s+[А-ЯЁ][а-яё]+\\s+[А-ЯЁ][а-яё]+"
        );

        Set<String> allTeacherNames = lessons.stream()
                .filter(lesson -> lesson.getTeacher() != null && !lesson.getTeacher().trim().isEmpty())
                .flatMap(lesson -> {
                    java.util.regex.Matcher matcher = teacherNamePattern.matcher(lesson.getTeacher());
                    List<String> teachers = new ArrayList<>();
                    while (matcher.find()) {
                        teachers.add(matcher.group().trim());
                    }
                    if (teachers.isEmpty()) {
                        // fallback: разбиваем по запятой или переносу строки
                        teachers.addAll(Arrays.asList(lesson.getTeacher().split("[,\n]")));
                    }
                    return teachers.stream().map(String::trim).filter(s -> !s.isEmpty());
                })
                .collect(Collectors.toSet());

        if (allTeacherNames.isEmpty()) {
            log.debug("Нет преподавателей для обработки");
            return;
        }

        log.debug("Обработка {} уникальных преподавателей", allTeacherNames.size());

        Map<String, TeacherEntity> existingTeachersMap = teacherRepository
                .findByFullNameIn(new ArrayList<>(allTeacherNames))
                .stream()
                .collect(Collectors.toMap(TeacherEntity::getFullName, Function.identity()));

        List<TeacherEntity> teachersToCreate = allTeacherNames.stream()
                .filter(teacherName -> !existingTeachersMap.containsKey(teacherName))
                .map(teacherName -> {
                    TeacherEntity newTeacher = new TeacherEntity();
                    newTeacher.setFullName(teacherName);
                    return newTeacher;
                })
                .collect(Collectors.toList());

        if (!teachersToCreate.isEmpty()) {
            log.debug("Создание {} новых преподавателей", teachersToCreate.size());
            List<TeacherEntity> savedTeachers = teacherRepository.saveAll(teachersToCreate);
            savedTeachers.forEach(teacher -> existingTeachersMap.put(teacher.getFullName(), teacher));
        }

        for (LessonEntity lesson : lessons) {
            if (lesson.getTeacher() != null && !lesson.getTeacher().trim().isEmpty()) {
                java.util.regex.Matcher matcher = teacherNamePattern.matcher(lesson.getTeacher());
                List<TeacherEntity> processedTeachers = new ArrayList<>();
                while (matcher.find()) {
                    String teacherName = matcher.group().trim();
                    TeacherEntity teacher = existingTeachersMap.get(teacherName);
                    if (teacher != null) {
                        processedTeachers.add(teacher);
                    }
                }
                if (!processedTeachers.isEmpty()) {
                    lesson.setTeachers(processedTeachers);
                }
            }
        }
    }

    private void processRoomsBatch(List<LessonEntity> lessons) {
        // Шаблон для аудитории: "БУКВА-ЦИФРЫ (КОРПУС)" или "БУКВА-ЦИФРЫ-БУКВА (КОРПУС)"
        java.util.regex.Pattern roomPattern = java.util.regex.Pattern.compile(
                "[А-ЯA-Z]-\\d+[а-яa-z]?\\s*\\([^)]+\\)"
        );

        Set<String> allRoomNames = lessons.stream()
                .filter(lesson -> lesson.getRoom() != null && !lesson.getRoom().trim().isEmpty())
                .flatMap(lesson -> {
                    java.util.regex.Matcher matcher = roomPattern.matcher(lesson.getRoom());
                    List<String> rooms = new ArrayList<>();
                    while (matcher.find()) {
                        rooms.add(matcher.group().trim());
                    }
                    if (rooms.isEmpty()) {
                        // fallback: разбиваем по запятой
                        rooms.addAll(Arrays.asList(lesson.getRoom().split(",")));
                    }
                    return rooms.stream().map(String::trim).filter(s -> !s.isEmpty());
                })
                .collect(Collectors.toSet());

        if (allRoomNames.isEmpty()) {
            log.debug("Нет аудиторий для обработки");
            return;
        }

        log.debug("Обработка {} уникальных аудиторий", allRoomNames.size());

        Map<String, RoomEntity> existingRoomsMap = roomRepository
                .findByRoomNamesIn(new ArrayList<>(allRoomNames))
                .stream()
                .collect(Collectors.toMap(RoomEntity::getRoomName, Function.identity()));

        List<RoomEntity> roomsToCreate = allRoomNames.stream()
                .filter(roomName -> !existingRoomsMap.containsKey(roomName))
                .map(roomName -> {
                    RoomEntity newRoom = new RoomEntity();
                    newRoom.setRoomName(roomName);
                    return newRoom;
                })
                .collect(Collectors.toList());

        if (!roomsToCreate.isEmpty()) {
            log.debug("Создание {} новых аудиторий", roomsToCreate.size());
            List<RoomEntity> savedRooms = roomRepository.saveAll(roomsToCreate);
            savedRooms.forEach(room -> existingRoomsMap.put(room.getRoomName(), room));
        }

        for (LessonEntity lesson : lessons) {
            if (lesson.getRoom() != null && !lesson.getRoom().trim().isEmpty()) {
                java.util.regex.Matcher matcher = roomPattern.matcher(lesson.getRoom());
                List<RoomEntity> processedRooms = new ArrayList<>();
                while (matcher.find()) {
                    String roomName = matcher.group().trim();
                    RoomEntity room = existingRoomsMap.get(roomName);
                    if (room != null) {
                        processedRooms.add(room);
                    }
                }
                if (!processedRooms.isEmpty()) {
                    lesson.setRooms(processedRooms);
                }
            }
        }
    }

    @Transactional
    public void saveLessonsWithErrorHandling(List<LessonEntity> lessons, String entityType, String entityName) {
        BatchSaveResult result = saveLessonsBatch(lessons, entityType, entityName);

        if (result.getErrorCount() > 0) {
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

        public int getSavedCount() { return savedCount; }
        public int getErrorCount() { return errorCount; }
        public int getTotalCount() { return totalCount; }
        public List<String> getErrors() { return new ArrayList<>(errors); }
        public boolean isSuccess() { return errorCount == 0; }
        public double getSuccessRate() {
            return totalCount > 0 ? (double) savedCount / totalCount * 100 : 0;
        }
    }
}