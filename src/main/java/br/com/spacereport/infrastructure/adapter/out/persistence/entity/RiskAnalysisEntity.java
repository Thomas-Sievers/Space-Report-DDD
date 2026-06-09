package br.com.spacereport.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "AnaliseRisco")
public class RiskAnalysisEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "IdAnalise")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IdEvento", nullable = false)
    private SpaceEventEntity spaceEvent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IdAtivo", nullable = false)
    private SpaceAssetEntity spaceAsset;

    @Column(name = "NivelRisco", nullable = false, length = 20)
    private String riskLevel;

    @Column(name = "ScoreRisco", nullable = false)
    private double riskScore;

    @Column(name = "DataAnalise", nullable = false)
    private LocalDate analysisDate;

    public RiskAnalysisEntity() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public SpaceEventEntity getSpaceEvent() { return spaceEvent; }
    public void setSpaceEvent(SpaceEventEntity spaceEvent) { this.spaceEvent = spaceEvent; }
    public SpaceAssetEntity getSpaceAsset() { return spaceAsset; }
    public void setSpaceAsset(SpaceAssetEntity spaceAsset) { this.spaceAsset = spaceAsset; }
    public String getRiskLevel() { return riskLevel; }
    public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }
    public double getRiskScore() { return riskScore; }
    public void setRiskScore(double riskScore) { this.riskScore = riskScore; }
    public LocalDate getAnalysisDate() { return analysisDate; }
    public void setAnalysisDate(LocalDate analysisDate) { this.analysisDate = analysisDate; }
}
