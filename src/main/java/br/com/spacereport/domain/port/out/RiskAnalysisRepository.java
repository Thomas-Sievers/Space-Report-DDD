package br.com.spacereport.domain.port.out;

import br.com.spacereport.domain.model.RiskAnalysis;
import br.com.spacereport.domain.model.SpaceAsset;

import java.util.List;
import java.util.Optional;

public interface RiskAnalysisRepository {
    RiskAnalysis save(RiskAnalysis riskAnalysis);
    Optional<RiskAnalysis> findById(Long id);
    List<RiskAnalysis> findByAsset(SpaceAsset spaceAsset);
}
