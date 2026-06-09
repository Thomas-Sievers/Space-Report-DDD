package br.com.spacereport.domain.port.in;

import br.com.spacereport.domain.model.Alert;
import br.com.spacereport.domain.model.RiskAnalysis;

import java.util.Optional;

public interface GenerateAlertUseCase {
    Optional<Alert> generate(RiskAnalysis riskAnalysis);
}
