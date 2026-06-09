package br.com.spacereport.infrastructure.adapter.out.persistence.repository;

import br.com.spacereport.infrastructure.adapter.out.persistence.entity.RiskAnalysisEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RiskAnalysisJpaRepository extends JpaRepository<RiskAnalysisEntity, Long> {
    List<RiskAnalysisEntity> findBySpaceAssetId(Long assetId);
}
