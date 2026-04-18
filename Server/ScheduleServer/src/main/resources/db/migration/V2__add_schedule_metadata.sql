-- Таблица для хранения метаданных о загруженных расписаниях
CREATE TABLE IF NOT EXISTS schedule_metadata (
    id BIGSERIAL PRIMARY KEY,
    entity_type VARCHAR(20) NOT NULL,      -- 'GROUP', 'TEACHER', 'ROOM'
    entity_name VARCHAR(255) NOT NULL,     -- 'ИВБО-01-22', 'Иванов И.И.', 'А-15'
    semester VARCHAR(20) NOT NULL,         -- '2026-SPRING', '2025-AUTUMN'
    last_updated TIMESTAMP NOT NULL,       -- когда последний раз обновляли
    lesson_count INTEGER DEFAULT 0,        -- сколько занятий сохранено
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT uk_entity_semester UNIQUE (entity_type, entity_name, semester)
);

-- Индекс для быстрого поиска по сущности и семестру
CREATE INDEX IF NOT EXISTS idx_schedule_metadata_lookup
ON schedule_metadata(entity_type, entity_name, semester);

-- Комментарии к таблице и колонкам
COMMENT ON TABLE schedule_metadata IS 'Метаданные о загруженных расписаниях';
COMMENT ON COLUMN schedule_metadata.entity_type IS 'Тип сущности: GROUP, TEACHER, ROOM';
COMMENT ON COLUMN schedule_metadata.entity_name IS 'Название сущности (группа, преподаватель, аудитория)';
COMMENT ON COLUMN schedule_metadata.semester IS 'Семестр в формате ГОД-SPRING или ГОД-AUTUMN';
COMMENT ON COLUMN schedule_metadata.last_updated IS 'Дата и время последнего обновления данных';
COMMENT ON COLUMN schedule_metadata.lesson_count IS 'Количество сохраненных занятий для этой сущности';