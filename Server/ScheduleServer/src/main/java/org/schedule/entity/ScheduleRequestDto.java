package org.schedule.entity;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public class ScheduleRequestDto {
    @NotEmpty(message = "Список сущностей не может быть пустым")
    private List<String> entities;

    // Конструкторы
    public ScheduleRequestDto() {}

    public ScheduleRequestDto(List<String> entities) {
        this.entities = entities;
    }

    // Getters and Setters
    public List<String> getEntities() { return entities; }
    public void setEntities(List<String> entities) { this.entities = entities; }
}