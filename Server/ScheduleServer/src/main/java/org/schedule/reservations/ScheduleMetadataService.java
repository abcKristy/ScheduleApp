package org.schedule.reservations;

import org.schedule.entity.ScheduleMetadataResponseDto;
import org.schedule.entity.forBD.EntityType;
import org.schedule.entity.forBD.ScheduleMetadataEntity;
import org.schedule.mapping.CheckDataInMemory;
import org.schedule.repository.ScheduleMetadataRepository;
import org.schedule.util.SemesterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ScheduleMetadataService {
    private static final Logger log = LoggerFactory.getLogger(ScheduleMetadataService.class);

    private final ScheduleMetadataRepository metadataRepository;
    private final CheckDataInMemory checkHelper;

    public ScheduleMetadataService(ScheduleMetadataRepository metadataRepository,
                                   CheckDataInMemory checkHelper) {
        this.metadataRepository = metadataRepository;
        this.checkHelper = checkHelper;
    }

    /**
     * Проверяет актуальность расписания для сущности
     */
    public ScheduleMetadataResponseDto checkMetadata(String entityString) {
        log.info("Проверка метаданных для: {}", entityString);

        try {
            CheckDataInMemory.EntityCheckResult checkResult = checkHelper.checkEntity(entityString);

            if (!checkResult.isValid()) {
                log.warn("Не удалось определить тип сущности для: {}", entityString);
                return createNotFoundResponse(entityString);
            }

            EntityType entityType = checkResult.getEntityType();
            String entityName = checkResult.getEntityName();
            String currentSemester = SemesterUtils.getCurrentSemester();

            Optional<ScheduleMetadataEntity> metadataOpt =
                    metadataRepository.findByEntityTypeAndEntityNameAndSemester(
                            entityType.name(), entityName, currentSemester);

            if (metadataOpt.isPresent()) {
                ScheduleMetadataEntity metadata = metadataOpt.get();
                ScheduleMetadataResponseDto response = new ScheduleMetadataResponseDto(
                        entityType.name(),
                        entityName,
                        metadata.getSemester(),
                        currentSemester,
                        metadata.getLastUpdated(),
                        metadata.getLessonCount()
                );
                response.setDisplayName(SemesterUtils.getDisplayName(metadata.getSemester()));

                log.info("Метаданные найдены: {} {}, семестр: {}, занятий: {}, требует обновления: {}",
                        entityType, entityName, metadata.getSemester(),
                        metadata.getLessonCount(), response.isNeedsUpdate());

                return response;
            } else {
                log.info("Метаданные не найдены для {} {} в семестре {}",
                        entityType, entityName, currentSemester);

                ScheduleMetadataResponseDto response = new ScheduleMetadataResponseDto(
                        entityType.name(),
                        entityName,
                        null,
                        currentSemester,
                        null,
                        0
                );
                response.setNeedsUpdate(true);

                return response;
            }

        } catch (Exception e) {
            log.error("Ошибка при проверке метаданных для: {}", entityString, e);
            throw new RuntimeException("Ошибка при проверке метаданных", e);
        }
    }

    /**
     * Проверяет актуальность для нескольких сущностей
     */
    public List<ScheduleMetadataResponseDto> checkMetadataBatch(List<String> entityStrings) {
        log.info("Пакетная проверка метаданных для {} сущностей", entityStrings.size());

        return entityStrings.stream()
                .map(this::checkMetadata)
                .collect(java.util.stream.Collectors.toList());
    }

    private ScheduleMetadataResponseDto createNotFoundResponse(String entityString) {
        ScheduleMetadataResponseDto response = new ScheduleMetadataResponseDto();
        response.setEntityName(entityString);
        response.setCurrentSemester(SemesterUtils.getCurrentSemester());
        response.setNeedsUpdate(true);
        return response;
    }
}