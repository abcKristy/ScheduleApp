-- Добавляем колонку semester в таблицу lessons
ALTER TABLE lessons
ADD COLUMN IF NOT EXISTS semester VARCHAR(20);

-- Устанавливаем значение по умолчанию для существующих записей
-- Помечаем старые записи как 'LEGACY'
UPDATE lessons
SET semester = 'LEGACY'
WHERE semester IS NULL;

-- Делаем колонку NOT NULL после заполнения
ALTER TABLE lessons
ALTER COLUMN semester SET NOT NULL;

-- Добавляем индекс для быстрого поиска по семестру
CREATE INDEX IF NOT EXISTS idx_lessons_semester ON lessons(semester);

-- Комментарий к колонке
COMMENT ON COLUMN lessons.semester IS 'Семестр, к которому относится занятие (2026-SPRING, 2025-AUTUMN, LEGACY)';