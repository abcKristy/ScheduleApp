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
import java.util.concurrent.CompletableFuture;
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
     * ПОЛНОЕ ПАКЕТНОЕ СОХРАНЕНИЕ УРОКОВ С УСИЛЕННОЙ ДЕДУПЛИКАЦИЕЙ
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
            // ЭТАП 0: УСИЛЕННАЯ ДЕДУПЛИКАЦИЯ С ПРОВЕРКОЙ БД
            log.debug("Этап 0: Усиленная дедупликация занятий");
            List<LessonEntity> deduplicatedLessons = deduplicateLessonsWithDBCheck(lessons);
            log.info("После дедупликации: {} -> {} занятий", lessons.size(), deduplicatedLessons.size());

            // ОПТИМИЗАЦИЯ N+1: обрабатываем ВСЕ справочники для ВСЕХ уроков пакетно
            log.debug("Этап 1: Пакетная обработка справочников");
            if (shouldUseParallelProcessing(deduplicatedLessons)) {
                log.debug("Используется параллельная обработка");
                processAllReferencesParallel(deduplicatedLessons);
            } else {
                log.debug("Используется последовательная обработка");
                processAllReferencesBatch(deduplicatedLessons);
            }

            // Сохраняем уроки
            log.debug("Этап 2: Пакетное сохранение дедуплицированных уроков");
            List<LessonEntity> savedLessons = lessonRepository.saveAll(deduplicatedLessons);

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
     * УСИЛЕННАЯ ДЕДУПЛИКАЦИЯ С ПРОВЕРКОЙ БД И ДВУХУРОВНЕВОЙ ПРОВЕРКОЙ
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

            // Уровень 1: Проверка дубликатов в текущей пачке
            if (seenKeysInCurrentBatch.contains(key)) {
                log.debug("Пропуск дубликата в текущей пачке: {}", key);
                duplicatesInBatch++;
                continue;
            }

            // Уровень 2: Проверка дубликатов в БД
            boolean existsInDB = checkIfLessonExistsInDatabase(lesson);
            if (existsInDB) {
                log.debug("Пропуск дубликата (уже есть в БД): {}", key);
                duplicatesInDB++;
                continue;
            }

            // Если не дубликат - добавляем
            result.add(lesson);
            seenKeysInCurrentBatch.add(key);
        }

        log.info("Дедупликация завершена. Оригиналов: {}, дубликатов в пачке: {}, дубликатов в БД: {}",
                result.size(), duplicatesInBatch, duplicatesInDB);

        return result;
    }

    /**
     * СОЗДАЕТ КЛЮЧ ДЕДУПЛИКАЦИИ
     */
    private String createDeduplicationKey(LessonEntity lesson) {
        return String.format("%s|%s|%s|%s|%s|%s",
                lesson.getDiscipline() != null ? lesson.getDiscipline().trim().toLowerCase() : "",
                lesson.getLessonType() != null ? lesson.getLessonType().name() : "",
                lesson.getStartTime() != null ? lesson.getStartTime().toString() : "",
                lesson.getEndTime() != null ? lesson.getEndTime().toString() : "",
                normalizeGroups(lesson.getGroupsSummary()),
                lesson.getTeacher() != null ? lesson.getTeacher().trim().toLowerCase() : "");
    }

    /**
     * Нормализует строку групп для корректного сравнения
     */
    private String normalizeGroups(String groupsSummary) {
        if (groupsSummary == null || groupsSummary.trim().isEmpty()) {
            return "";
        }

        // Разделяем группы, сортируем и объединяем обратно
        return Arrays.stream(groupsSummary.split(",\\s*"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .sorted()
                .collect(Collectors.joining(", "))
                .toLowerCase();
    }

    /**
     * Проверяет существование занятия в БД
     */
    private boolean checkIfLessonExistsInDatabase(LessonEntity lesson) {
        try {
            // Ищем занятия с такими же основными параметрами
            List<LessonEntity> existingLessons = lessonRepository.findByDisciplineAndStartTimeAndEndTime(
                    lesson.getDiscipline(),
                    lesson.getStartTime(),
                    lesson.getEndTime()
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
            return false; // В случае ошибки лучше пропустить проверку, чем потерять данные
        }
    }

    /**
     * Сравнивает два занятия на идентичность
     */
    private boolean isSameLesson(LessonEntity lesson1, LessonEntity lesson2) {
        boolean isSame = Objects.equals(lesson1.getDiscipline(), lesson2.getDiscipline()) &&
                Objects.equals(lesson1.getLessonType(), lesson2.getLessonType()) &&
                Objects.equals(lesson1.getStartTime(), lesson2.getStartTime()) &&
                Objects.equals(lesson1.getEndTime(), lesson2.getEndTime()) &&
                Objects.equals(normalizeGroups(lesson1.getGroupsSummary()), normalizeGroups(lesson2.getGroupsSummary())) &&
                Objects.equals(
                        lesson1.getTeacher() != null ? lesson1.getTeacher().trim().toLowerCase() : "",
                        lesson2.getTeacher() != null ? lesson2.getTeacher().trim().toLowerCase() : ""
                );

        if (isSame) {
            log.debug("Занятия идентичны: {}", createDeduplicationKey(lesson1));
        }

        return isSame;
    }

    private boolean shouldUseParallelProcessing(List<LessonEntity> lessons) {
        // Используем параллельную обработку для больших объемов данных
        int threshold = 100; // Настройка порога

        if (lessons.size() < threshold) {
            return false; // Маленькие объемы - последовательно (меньше накладных расходов)
        }

        // Проверяем сложность данных
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

        return totalReferences > 500; // Много справочников - используем параллелизм
    }

    private void processAllReferencesBatch(List<LessonEntity> lessons) {
        log.debug("Пакетная обработка справочников для {} уроков", lessons.size());

        processGroupsBatch(lessons);
        processTeachersBatch(lessons);
        processRoomsBatch(lessons);
    }

    /**
     * ПАРАЛЛЕЛЬНАЯ обработка справочников
     */
    private void processAllReferencesParallel(List<LessonEntity> lessons) {
        log.debug("Параллельная обработка справочников для {} уроков", lessons.size());

        // Создаем задачи для параллельного выполнения
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

        // Ждем завершения ВСЕХ задач
        try {
            CompletableFuture.allOf(groupsFuture, teachersFuture, roomsFuture).join();
            log.debug("Параллельная обработка справочников завершена");
        } catch (Exception e) {
            log.error("Критическая ошибка при параллельной обработке: {}", e.getMessage());
            throw new RuntimeException("Не удалось выполнить параллельную обработку", e);
        }
    }

    /**
     * ОПТИМИЗИРОВАННЫЙ метод обработки групп (N+1 решение)
     */
    private void processGroupsBatch(List<LessonEntity> lessons) {
        // 1. Собираем ВСЕ уникальные имена групп из ВСЕХ уроков
        Set<String> allGroupNames = lessons.stream()
                .filter(lesson -> lesson.getGroups() != null)
                .flatMap(lesson -> lesson.getGroups().stream())
                .map(GroupEntity::getGroupName)
                .filter(name -> name != null && !name.trim().isEmpty())
                .collect(Collectors.toSet());

        if (allGroupNames.isEmpty()) {
            log.debug("Нет групп для обработки");
            return;
        }

        log.debug("Обработка {} уникальных групп", allGroupNames.size());

        // 2. ОДИН запрос для получения всех существующих групп
        Map<String, GroupEntity> existingGroupsMap = groupRepository
                .findByGroupNameIn(new ArrayList<>(allGroupNames))
                .stream()
                .collect(Collectors.toMap(GroupEntity::getGroupName, Function.identity()));

        // 3. Определяем какие группы нужно создать
        List<GroupEntity> groupsToCreate = allGroupNames.stream()
                .filter(groupName -> !existingGroupsMap.containsKey(groupName))
                .map(groupName -> {
                    GroupEntity newGroup = new GroupEntity();
                    newGroup.setGroupName(groupName);
                    return newGroup;
                })
                .collect(Collectors.toList());

        // 4. ОДИН запрос для сохранения всех новых групп
        if (!groupsToCreate.isEmpty()) {
            log.debug("Создание {} новых групп", groupsToCreate.size());
            List<GroupEntity> savedGroups = groupRepository.saveAll(groupsToCreate);
            savedGroups.forEach(group -> existingGroupsMap.put(group.getGroupName(), group));
        }

        // 5. Сопоставляем группы с уроками (в памяти, без запросов)
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

    /**
     * ОПТИМИЗИРОВАННЫЙ метод обработки преподавателей (N+1 решение)
     */
    private void processTeachersBatch(List<LessonEntity> lessons) {
        // 1. Собираем ВСЕ уникальные имена преподавателей из ВСЕХ уроков
        Set<String> allTeacherNames = lessons.stream()
                .filter(lesson -> lesson.getTeacher() != null)
                .flatMap(lesson -> Arrays.stream(lesson.getTeacher().split("[,\n]")))
                .map(String::trim)
                .filter(name -> !name.isEmpty())
                .collect(Collectors.toSet());

        if (allTeacherNames.isEmpty()) {
            log.debug("Нет преподавателей для обработки");
            return;
        }

        log.debug("Обработка {} уникальных преподавателей", allTeacherNames.size());

        // 2. ОДИН запрос для получения всех существующих преподавателей
        Map<String, TeacherEntity> existingTeachersMap = teacherRepository
                .findByFullNameIn(new ArrayList<>(allTeacherNames))
                .stream()
                .collect(Collectors.toMap(TeacherEntity::getFullName, Function.identity()));

        // 3. Создаем новых преподавателей
        List<TeacherEntity> teachersToCreate = allTeacherNames.stream()
                .filter(teacherName -> !existingTeachersMap.containsKey(teacherName))
                .map(teacherName -> {
                    TeacherEntity newTeacher = new TeacherEntity();
                    newTeacher.setFullName(teacherName);
                    return newTeacher;
                })
                .collect(Collectors.toList());

        // 4. ОДИН запрос для сохранения всех новых преподавателей
        if (!teachersToCreate.isEmpty()) {
            log.debug("Создание {} новых преподавателей", teachersToCreate.size());
            List<TeacherEntity> savedTeachers = teacherRepository.saveAll(teachersToCreate);
            savedTeachers.forEach(teacher -> existingTeachersMap.put(teacher.getFullName(), teacher));
        }

        // 5. Сопоставляем преподавателей с уроками
        for (LessonEntity lesson : lessons) {
            if (lesson.getTeacher() != null) {
                List<TeacherEntity> processedTeachers = Arrays.stream(lesson.getTeacher().split("[,\n]"))
                        .map(String::trim)
                        .filter(name -> !name.isEmpty())
                        .map(existingTeachersMap::get)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
                lesson.setTeachers(processedTeachers);
            }
        }
    }

    /**
     * ОПТИМИЗИРОВАННЫЙ метод обработки аудиторий (N+1 решение)
     */
    private void processRoomsBatch(List<LessonEntity> lessons) {
        // 1. Собираем ВСЕ уникальные имена аудиторий из ВСЕХ уроков
        Set<String> allRoomNames = lessons.stream()
                .filter(lesson -> lesson.getRoom() != null)
                .flatMap(lesson -> Arrays.stream(lesson.getRoom().split(",")))
                .map(String::trim)
                .filter(name -> !name.isEmpty())
                .collect(Collectors.toSet());

        if (allRoomNames.isEmpty()) {
            log.debug("Нет аудиторий для обработки");
            return;
        }

        log.debug("Обработка {} уникальных аудиторий", allRoomNames.size());

        // 2. ОДИН запрос для получения всех существующих аудиторий
        Map<String, RoomEntity> existingRoomsMap = roomRepository
                .findByRoomNamesIn(new ArrayList<>(allRoomNames))
                .stream()
                .collect(Collectors.toMap(RoomEntity::getRoomName, Function.identity()));

        // 3. Создаем новых аудиторий
        List<RoomEntity> roomsToCreate = allRoomNames.stream()
                .filter(roomName -> !existingRoomsMap.containsKey(roomName))
                .map(roomName -> {
                    RoomEntity newRoom = new RoomEntity();
                    newRoom.setRoomName(roomName);
                    return newRoom;
                })
                .collect(Collectors.toList());

        // 4. ОДИН запрос для сохранения всех новых аудиторий
        if (!roomsToCreate.isEmpty()) {
            log.debug("Создание {} новых аудиторий", roomsToCreate.size());
            List<RoomEntity> savedRooms = roomRepository.saveAll(roomsToCreate);
            savedRooms.forEach(room -> existingRoomsMap.put(room.getRoomName(), room));
        }

        // 5. Сопоставляем аудитории с уроками
        for (LessonEntity lesson : lessons) {
            if (lesson.getRoom() != null) {
                List<RoomEntity> processedRooms = Arrays.stream(lesson.getRoom().split(","))
                        .map(String::trim)
                        .filter(name -> !name.isEmpty())
                        .map(existingRoomsMap::get)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());
                lesson.setRooms(processedRooms);
            }
        }
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
}