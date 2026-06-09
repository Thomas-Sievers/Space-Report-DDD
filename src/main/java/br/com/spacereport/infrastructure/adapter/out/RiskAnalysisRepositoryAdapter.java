package br.com.spacereport.infrastructure.adapter.out;

import br.com.spacereport.domain.model.*;
import br.com.spacereport.domain.port.out.RiskAnalysisRepository;
import br.com.spacereport.infrastructure.adapter.out.persistence.entity.*;
import br.com.spacereport.infrastructure.adapter.out.persistence.repository.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
@Transactional
public class RiskAnalysisRepositoryAdapter implements RiskAnalysisRepository {

    private final RiskAnalysisJpaRepository jpaRepository;
    private final SpaceEventJpaRepository eventJpaRepository;
    private final SpaceAssetJpaRepository assetJpaRepository;

    public RiskAnalysisRepositoryAdapter(RiskAnalysisJpaRepository jpaRepository,
                                         SpaceEventJpaRepository eventJpaRepository,
                                         SpaceAssetJpaRepository assetJpaRepository) {
        this.jpaRepository = jpaRepository;
        this.eventJpaRepository = eventJpaRepository;
        this.assetJpaRepository = assetJpaRepository;
    }

    @Override
    public RiskAnalysis save(RiskAnalysis riskAnalysis) {
        RiskAnalysisEntity entity = toEntity(riskAnalysis);
        return toDomain(jpaRepository.save(entity));
    }

    @Override
    public RiskAnalysis update(RiskAnalysis riskAnalysis) {
        return toDomain(jpaRepository.save(toEntity(riskAnalysis)));
    }

    @Override
    public void delete(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public Optional<RiskAnalysis> findById(Long id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<RiskAnalysis> findByAsset(SpaceAsset spaceAsset) {
        return jpaRepository.findBySpaceAssetId(spaceAsset.getId())
                .stream().map(this::toDomain).toList();
    }

    private RiskAnalysisEntity toEntity(RiskAnalysis r) {
        RiskAnalysisEntity entity = new RiskAnalysisEntity();
        entity.setId(r.getId());
        entity.setRiskLevel(r.getRiskLevel().name());
        entity.setRiskScore(r.getRiskScore());
        entity.setAnalysisDate(r.getAnalysisDate());

        SpaceEventEntity eventEntity = eventJpaRepository.findById(r.getSpaceEvent().getId())
                .orElseThrow(() -> new IllegalStateException("SpaceEvent not found: " + r.getSpaceEvent().getId()));
        entity.setSpaceEvent(eventEntity);

        SpaceAssetEntity assetEntity = assetJpaRepository.findById(r.getSpaceAsset().getId())
                .orElseThrow(() -> new IllegalStateException("SpaceAsset not found: " + r.getSpaceAsset().getId()));
        entity.setSpaceAsset(assetEntity);

        return entity;
    }

    private RiskAnalysis toDomain(RiskAnalysisEntity e) {
        SpaceEventEntity se = e.getSpaceEvent();
        SpaceEvent spaceEvent = new SpaceEvent(se.getId(), EventType.valueOf(se.getEventType()),
                se.getClassification(), se.getIntensity(), se.getEventDate(), se.getDescription());

        SpaceAssetEntity sa = e.getSpaceAsset();
        OrganizationEntity org = sa.getOrganization();
        Organization domainOrg = new Organization(org.getId(), org.getName(), org.getType(),
                org.getContactEmail(), org.getCountry());
        SpaceAsset spaceAsset = new SpaceAsset(sa.getId(), sa.getName(), AssetType.valueOf(sa.getAssetType()),
                sa.getOrbit(), sa.getOperationalStatus(), domainOrg);

        return new RiskAnalysis(e.getId(), spaceEvent, spaceAsset,
                RiskLevel.valueOf(e.getRiskLevel()), e.getRiskScore(), e.getAnalysisDate());
    }
}
