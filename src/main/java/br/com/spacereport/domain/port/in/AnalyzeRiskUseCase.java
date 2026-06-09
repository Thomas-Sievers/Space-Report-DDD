package br.com.spacereport.domain.port.in;

import br.com.spacereport.domain.model.RiskAnalysis;
import br.com.spacereport.domain.model.SpaceAsset;
import br.com.spacereport.domain.model.SpaceEvent;

public interface AnalyzeRiskUseCase {
    RiskAnalysis analyze(SpaceEvent spaceEvent, SpaceAsset spaceAsset);
}
