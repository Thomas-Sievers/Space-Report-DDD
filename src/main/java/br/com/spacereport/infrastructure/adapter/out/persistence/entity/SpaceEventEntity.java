package br.com.spacereport.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "EventoEspacial")
public class SpaceEventEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "IdEvento")
    private Long id;

    @Column(name = "TipoEvento", nullable = false, length = 50)
    private String eventType;

    @Column(name = "Classificacao", length = 20)
    private String classification;

    @Column(name = "Intensidade", nullable = false)
    private double intensity;

    @Column(name = "DataEvento", nullable = false)
    private LocalDate eventDate;

    @Column(name = "Descricao", length = 300)
    private String description;

    public SpaceEventEntity() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }
    public String getClassification() { return classification; }
    public void setClassification(String classification) { this.classification = classification; }
    public double getIntensity() { return intensity; }
    public void setIntensity(double intensity) { this.intensity = intensity; }
    public LocalDate getEventDate() { return eventDate; }
    public void setEventDate(LocalDate eventDate) { this.eventDate = eventDate; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
