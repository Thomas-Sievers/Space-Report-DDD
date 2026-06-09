package br.com.spacereport.domain.service;

import br.com.spacereport.domain.model.*;
import br.com.spacereport.domain.port.out.AlertNotifier;
import br.com.spacereport.domain.port.out.AlertRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlertServiceTest {

    @Mock
    private AlertRepository alertRepository;

    @Mock
    private AlertNotifier alertNotifier;

    private AlertService service;
    private RiskAnalysis criticalAnalysis;
    private RiskAnalysis lowAnalysis;

    @BeforeEach
    void setUp() {
        service = new AlertService(alertRepository, alertNotifier);

        Organization org = new Organization(1L, "Org", "Agency", "http://webhook.org/alert", "BR");
        SpaceAsset asset = new SpaceAsset(1L, "SAT-01", AssetType.SATELLITE, "LEO", "OPERATIONAL", org);
        SpaceEvent event = new SpaceEvent(1L, EventType.CME, null, 900.0, LocalDate.now(), "Fast CME");

        criticalAnalysis = new RiskAnalysis(1L, event, asset, RiskLevel.CRITICAL, 900.0, LocalDate.now());
        lowAnalysis = new RiskAnalysis(2L, event, asset, RiskLevel.LOW, 10.0, LocalDate.now());
    }

    @Test
    void shouldGenerateAlertForCriticalRiskAnalysis() {
        when(alertRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        Optional<Alert> result = service.generate(criticalAnalysis);

        assertThat(result).isPresent();
        assertThat(result.get().getRiskAnalysis().getRiskLevel()).isEqualTo(RiskLevel.CRITICAL);
    }

    @Test
    void shouldNotGenerateAlertForLowRiskAnalysis() {
        Optional<Alert> result = service.generate(lowAnalysis);

        assertThat(result).isEmpty();
        verify(alertRepository, never()).save(any());
    }

    @Test
    void shouldCallNotifierOnCriticalAlert() {
        when(alertRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        service.generate(criticalAnalysis);

        verify(alertNotifier, times(1)).notify(any(Alert.class));
    }

    @Test
    void shouldNotCallNotifierForLowRiskAnalysis() {
        service.generate(lowAnalysis);

        verify(alertNotifier, never()).notify(any());
    }

    @Test
    void shouldReturnCriticalAlertsOnly() {
        Organization org = new Organization(1L, "Org", "Agency", "contact@org.com", "BR");
        SpaceAsset asset = new SpaceAsset(1L, "SAT-01", AssetType.SATELLITE, "LEO", "OPERATIONAL", org);
        SpaceEvent event = new SpaceEvent(1L, EventType.CME, null, 900.0, LocalDate.now(), "Fast CME");

        Alert criticalAlert = new Alert(1L, criticalAnalysis, "CRITICAL", LocalDate.now(), AlertStatus.PENDING);
        RiskAnalysis mediumAnalysis = new RiskAnalysis(3L, event, asset, RiskLevel.MEDIUM, 200.0, LocalDate.now());
        Alert mediumAlert = new Alert(2L, mediumAnalysis, "MEDIUM", LocalDate.now(), AlertStatus.PENDING);

        when(alertRepository.findAll()).thenReturn(List.of(criticalAlert, mediumAlert));

        List<Alert> result = service.findCritical();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getRiskAnalysis().getRiskLevel()).isEqualTo(RiskLevel.CRITICAL);
    }
}
