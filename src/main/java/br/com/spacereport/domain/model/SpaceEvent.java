package br.com.spacereport.domain.model;

import java.time.LocalDate;

public class SpaceEvent {

    private final Long id;
    private final EventType eventType;
    private final String classification;
    private final double intensity;
    private final LocalDate eventDate;
    private final String description;

    public SpaceEvent(Long id, EventType eventType, String classification,
                      double intensity, LocalDate eventDate, String description) {
        this.id = id;
        this.eventType = eventType;
        this.classification = classification;
        this.intensity = intensity;
        this.eventDate = eventDate;
        this.description = description;
    }

    public Long getId() { return id; }
    public EventType getEventType() { return eventType; }
    public String getClassification() { return classification; }
    public double getIntensity() { return intensity; }
    public LocalDate getEventDate() { return eventDate; }
    public String getDescription() { return description; }

    @Override
    public String toString() {
        return "SpaceEvent{id=" + id + ", type=" + eventType +
               ", intensity=" + intensity + ", date=" + eventDate + "}";
    }
}
