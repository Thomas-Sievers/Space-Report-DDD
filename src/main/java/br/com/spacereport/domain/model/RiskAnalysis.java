package br.com.spacereport.domain.model;

import java.time.LocalDate;

public class RiskAnalysis {

    private final Long id;
    private final SpaceEvent spaceEvent;
    private final SpaceAsset spaceAsset;
    private final RiskLevel riskLevel;
    private final double riskScore;
    private final LocalDate analysisDate;

    public RiskAnalysis(Long id, SpaceEvent spaceEvent, SpaceAsset spaceAsset,
                        RiskLevel riskLevel, double riskScore, LocalDate analysisDate) {
        this.id = id;
        this.spaceEvent = spaceEvent;
        this.spaceAsset = spaceAsset;
        this.riskLevel = riskLevel;
        this.riskScore = riskScore;
        this.analysisDate = analysisDate;
    }

    public Long getId() { return id; }
    public SpaceEvent getSpaceEvent() { return spaceEvent; }
    public SpaceAsset getSpaceAsset() { return spaceAsset; }
    public RiskLevel getRiskLevel() { return riskLevel; }
    public double getRiskScore() { return riskScore; }
    public LocalDate getAnalysisDate() { return analysisDate; }

    @Override
    public String toString() {
        return "RiskAnalysis{id=" + id + ", asset=" + spaceAsset.getName() +
               ", level=" + riskLevel + ", score=" + riskScore + "}";
    }
}
