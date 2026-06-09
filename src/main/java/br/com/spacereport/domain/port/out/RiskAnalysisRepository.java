package br.com.spacereport.domain.port.out;

import br.com.spacereport.domain.model.RiskAnalysis;
import br.com.spacereport.domain.model.SpaceAsset;

import java.util.List;
import java.util.Optional;

public interface RiskAnalysisRepository {
    RiskAnalysis save(RiskAnalysis riskAnalysis);
    RiskAnalysis update(RiskAnalysis riskAnalysis);
    void delete(Long id);
    Optional<RiskAnalysis> findById(Long id);
    List<RiskAnalysis> findByAsset(SpaceAsset spaceAsset);
}
