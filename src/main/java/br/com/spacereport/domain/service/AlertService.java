package br.com.spacereport.domain.service;

import br.com.spacereport.domain.model.Alert;
import br.com.spacereport.domain.model.AlertStatus;
import br.com.spacereport.domain.model.RiskAnalysis;
import br.com.spacereport.domain.model.RiskLevel;
import br.com.spacereport.domain.port.in.GenerateAlertUseCase;
import br.com.spacereport.domain.port.in.QueryAlertUseCase;
import br.com.spacereport.domain.port.out.AlertNotifier;
import br.com.spacereport.domain.port.out.AlertRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class AlertService implements GenerateAlertUseCase, QueryAlertUseCase {

    private final AlertRepository alertRepository;
    private final AlertNotifier alertNotifier;

    public AlertService(AlertRepository alertRepository, AlertNotifier alertNotifier) {
        this.alertRepository = alertRepository;
        this.alertNotifier = alertNotifier;
    }

    @Override
    public Optional<Alert> generate(RiskAnalysis riskAnalysis) {
        if (riskAnalysis.getRiskLevel() == RiskLevel.LOW) {
            return Optional.empty();
        }
        String message = buildAlertMessage(riskAnalysis);
        Alert alert = new Alert(
                null,
                riskAnalysis,
                message,
                LocalDate.now(),
                AlertStatus.PENDING
        );
        Alert saved = alertRepository.save(alert);
        alertNotifier.notify(saved);
        return Optional.of(saved);
    }

    @Override
    public List<Alert> findCritical() {
        return alertRepository.findAll().stream()
                .filter(a -> a.getRiskAnalysis().getRiskLevel() == RiskLevel.CRITICAL)
                .toList();
    }

    @Override
    public List<Alert> findAll() {
        return alertRepository.findAll();
    }

    private String buildAlertMessage(RiskAnalysis analysis) {
        return String.format(
                "[%s] Space event %s detected — asset '%s' at risk. Score: %.2f",
                analysis.getRiskLevel(),
                analysis.getSpaceEvent().getEventType(),
                analysis.getSpaceAsset().getName(),
                analysis.getRiskScore()
        );
    }
}
