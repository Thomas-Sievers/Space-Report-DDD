package br.com.spacereport.domain.service;

import br.com.spacereport.domain.model.EventType;
import br.com.spacereport.domain.model.RiskAnalysis;
import br.com.spacereport.domain.model.RiskLevel;
import br.com.spacereport.domain.model.SpaceAsset;
import br.com.spacereport.domain.model.SpaceEvent;
import br.com.spacereport.domain.port.in.AnalyzeRiskUseCase;
import br.com.spacereport.domain.port.out.RiskAnalysisRepository;

import java.time.LocalDate;

public class RiskAnalysisService implements AnalyzeRiskUseCase {

    // BR-001 thresholds — plasma speed (km/s) and proton flux (pfu) by orbit type
    private static final double CME_SPEED_THRESHOLD_LEO_KMS = 800.0;
    private static final double CME_SPEED_THRESHOLD_GEO_KMS = 600.0;
    private static final double CME_SPEED_THRESHOLD_LUNAR_KMS = 400.0;

    private static final double SEP_FLUX_THRESHOLD_LEO_PFU = 100.0;
    private static final double SEP_FLUX_THRESHOLD_GEO_PFU = 10.0;
    private static final double SEP_FLUX_THRESHOLD_LUNAR_PFU = 1.0;

    private final RiskAnalysisRepository riskAnalysisRepository;

    public RiskAnalysisService(RiskAnalysisRepository riskAnalysisRepository) {
        this.riskAnalysisRepository = riskAnalysisRepository;
    }

    @Override
    public RiskAnalysis analyze(SpaceEvent spaceEvent, SpaceAsset spaceAsset) {
        double riskScore = calculateRiskScore(spaceEvent, spaceAsset);
        RiskLevel riskLevel = determineRiskLevel(spaceEvent, spaceAsset, riskScore);
        RiskAnalysis analysis = new RiskAnalysis(
                null,
                spaceEvent,
                spaceAsset,
                riskLevel,
                riskScore,
                LocalDate.now()
        );
        return riskAnalysisRepository.save(analysis);
    }

    private double calculateRiskScore(SpaceEvent event, SpaceAsset asset) {
        double orbitMultiplier = switch (orbitCategory(asset.getOrbit())) {
            case "LEO" -> 1.0;
            case "GEO" -> 0.8;
            case "LUNAR" -> 1.2;
            default -> 0.6;
        };
        return event.getIntensity() * orbitMultiplier;
    }

    // BR-001: CRITICAL only when BOTH CME speed AND SEP flux exceed orbit thresholds
    private RiskLevel determineRiskLevel(SpaceEvent event, SpaceAsset asset, double riskScore) {
        String orbit = orbitCategory(asset.getOrbit());
        boolean cmeExceeds = event.getEventType() == EventType.CME
                && event.getIntensity() >= cmeThresholdFor(orbit);
        boolean sepExceeds = event.getEventType() == EventType.SEP
                && event.getIntensity() >= sepThresholdFor(orbit);

        if (cmeExceeds || sepExceeds) {
            return RiskLevel.CRITICAL;
        }
        if (riskScore >= 500) return RiskLevel.HIGH;
        if (riskScore >= 100) return RiskLevel.MEDIUM;
        return RiskLevel.LOW;
    }

    private String orbitCategory(String orbit) {
        if (orbit == null) return "UNKNOWN";
        String upper = orbit.toUpperCase();
        if (upper.contains("LEO")) return "LEO";
        if (upper.contains("GEO")) return "GEO";
        if (upper.contains("LUNAR") || upper.contains("MOON")) return "LUNAR";
        return "UNKNOWN";
    }

    private double cmeThresholdFor(String orbit) {
        return switch (orbit) {
            case "LEO" -> CME_SPEED_THRESHOLD_LEO_KMS;
            case "GEO" -> CME_SPEED_THRESHOLD_GEO_KMS;
            case "LUNAR" -> CME_SPEED_THRESHOLD_LUNAR_KMS;
            default -> CME_SPEED_THRESHOLD_GEO_KMS;
        };
    }

    private double sepThresholdFor(String orbit) {
        return switch (orbit) {
            case "LEO" -> SEP_FLUX_THRESHOLD_LEO_PFU;
            case "GEO" -> SEP_FLUX_THRESHOLD_GEO_PFU;
            case "LUNAR" -> SEP_FLUX_THRESHOLD_LUNAR_PFU;
            default -> SEP_FLUX_THRESHOLD_GEO_PFU;
        };
    }
}
