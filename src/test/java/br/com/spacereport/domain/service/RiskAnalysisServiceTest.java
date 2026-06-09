package br.com.spacereport.domain.service;

import br.com.spacereport.domain.model.*;
import br.com.spacereport.domain.port.out.RiskAnalysisRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RiskAnalysisServiceTest {

    @Mock
    private RiskAnalysisRepository riskAnalysisRepository;

    private RiskAnalysisService service;

    private Organization organization;
    private SpaceAsset leoAsset;
    private SpaceAsset geoAsset;
    private SpaceAsset lunarAsset;

    @BeforeEach
    void setUp() {
        service = new RiskAnalysisService(riskAnalysisRepository);
        when(riskAnalysisRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        organization = new Organization(1L, "TestOrg", "Agency", "test@org.com", "BR");
        leoAsset = new SpaceAsset(1L, "LEO-SAT", AssetType.SATELLITE, "LEO", "OPERATIONAL", organization);
        geoAsset = new SpaceAsset(2L, "GEO-SAT", AssetType.SATELLITE, "GEO", "OPERATIONAL", organization);
        lunarAsset = new SpaceAsset(3L, "Moon-Base", AssetType.LUNAR_BASE, "LUNAR", "OPERATIONAL", organization);
    }

    // BR-001: CME speed >= 800 km/s on LEO → CRITICAL
    @Test
    void shouldGenerateCriticalWhenCmeSpeedExceedsLeoThreshold() {
        SpaceEvent fastCme = new SpaceEvent(1L, EventType.CME, "C1.0", 850.0,
                LocalDate.now(), "Fast CME");

        RiskAnalysis result = service.analyze(fastCme, leoAsset);

        assertThat(result.getRiskLevel()).isEqualTo(RiskLevel.CRITICAL);
    }

    // BR-001: CME speed < 800 km/s on LEO → not CRITICAL
    @Test
    void shouldNotGenerateCriticalWhenCmeSpeedBelowLeoThreshold() {
        SpaceEvent slowCme = new SpaceEvent(2L, EventType.CME, "C1.0", 500.0,
                LocalDate.now(), "Slow CME");

        RiskAnalysis result = service.analyze(slowCme, leoAsset);

        assertThat(result.getRiskLevel()).isNotEqualTo(RiskLevel.CRITICAL);
    }

    // BR-001: SEP flux >= 100 pfu on LEO → CRITICAL
    @Test
    void shouldGenerateCriticalWhenSepFluxExceedsLeoThreshold() {
        SpaceEvent highSep = new SpaceEvent(3L, EventType.SEP, null, 150.0,
                LocalDate.now(), "High SEP flux");

        RiskAnalysis result = service.analyze(highSep, leoAsset);

        assertThat(result.getRiskLevel()).isEqualTo(RiskLevel.CRITICAL);
    }

    // SEP flux < 1 pfu on LUNAR → not CRITICAL
    @Test
    void shouldNotGenerateCriticalWhenSepFluxBelowLunarThreshold() {
        SpaceEvent lowSep = new SpaceEvent(4L, EventType.SEP, null, 0.5,
                LocalDate.now(), "Low SEP");

        RiskAnalysis result = service.analyze(lowSep, lunarAsset);

        assertThat(result.getRiskLevel()).isNotEqualTo(RiskLevel.CRITICAL);
    }

    // GEO threshold is lower (600 km/s) — CME at 700 is CRITICAL for GEO but not for LEO (needs 800)
    @Test
    void shouldGenerateCriticalForGeoAssetWithModerateCmeSpeed() {
        SpaceEvent moderateCme = new SpaceEvent(5L, EventType.CME, "M1.0", 650.0,
                LocalDate.now(), "Moderate CME");

        RiskAnalysis geoResult = service.analyze(moderateCme, geoAsset);
        RiskAnalysis leoResult = service.analyze(moderateCme, leoAsset);

        assertThat(geoResult.getRiskLevel()).isEqualTo(RiskLevel.CRITICAL);
        assertThat(leoResult.getRiskLevel()).isNotEqualTo(RiskLevel.CRITICAL);
    }

    @Test
    void shouldAssignRiskScoreGreaterThanZeroForAnyEvent() {
        SpaceEvent event = new SpaceEvent(6L, EventType.SOLAR_FLARE, "X1.0", 200.0,
                LocalDate.now(), "Solar flare");

        RiskAnalysis result = service.analyze(event, leoAsset);

        assertThat(result.getRiskScore()).isGreaterThan(0);
    }
}
