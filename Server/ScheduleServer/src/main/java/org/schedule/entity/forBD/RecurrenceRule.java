package org.schedule.entity.forBD;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.time.LocalDateTime;

@Embeddable
public class RecurrenceRule {
    @Column(name = "recurrence_frequency")
    private String frequency;

    @Column(name = "recurrence_interval")
    private Integer interval;

    @Column(name = "recurrence_until")
    private LocalDateTime until;

    // Геттеры и сеттеры
    public String getFrequency() { return frequency; }
    public void setFrequency(String frequency) { this.frequency = frequency; }

    public Integer getInterval() { return interval; }
    public void setInterval(Integer interval) { this.interval = interval; }

    public LocalDateTime getUntil() { return until; }
    public void setUntil(LocalDateTime until) { this.until = until; }
}