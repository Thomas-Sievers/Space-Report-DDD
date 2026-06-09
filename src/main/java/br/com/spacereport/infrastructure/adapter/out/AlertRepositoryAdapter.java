package br.com.spacereport.infrastructure.adapter.out;

import br.com.spacereport.domain.model.*;
import br.com.spacereport.domain.port.out.AlertRepository;
import br.com.spacereport.infrastructure.adapter.out.persistence.entity.*;
import br.com.spacereport.infrastructure.adapter.out.persistence.repository.*;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
@Transactional
public class AlertRepositoryAdapter implements AlertRepository {

    private final AlertJpaRepository jpaRepository;
    private final RiskAnalysisJpaRepository riskAnalysisJpaRepository;

    public AlertRepositoryAdapter(AlertJpaRepository jpaRepository,
                                  RiskAnalysisJpaRepository riskAnalysisJpaRepository) {
        this.jpaRepository = jpaRepository;
        this.riskAnalysisJpaRepository = riskAnalysisJpaRepository;
    }

    @Override
    public Alert save(Alert alert) {
        AlertEntity entity = toEntity(alert);
        return toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<Alert> findById(Long id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Alert> findByStatus(AlertStatus status) {
        return jpaRepository.findByStatus(status.name()).stream().map(this::toDomain).toList();
    }

    @Override
    public List<Alert> findAll() {
        return jpaRepository.findAll().stream().map(this::toDomain).toList();
    }

    private AlertEntity toEntity(Alert a) {
        AlertEntity entity = new AlertEntity();
        entity.setId(a.getId());
        entity.setMessage(a.getMessage());
        entity.setIssueDate(a.getIssueDate());
        entity.setStatus(a.getStatus().name());
        RiskAnalysisEntity raEntity = riskAnalysisJpaRepository.findById(a.getRiskAnalysis().getId())
                .orElseThrow(() -> new IllegalStateException("RiskAnalysis not found: " + a.getRiskAnalysis().getId()));
        entity.setRiskAnalysis(raEntity);
        return entity;
    }

    private Alert toDomain(AlertEntity e) {
        RiskAnalysisEntity ra = e.getRiskAnalysis();
        SpaceEventEntity se = ra.getSpaceEvent();
        SpaceEvent spaceEvent = new SpaceEvent(se.getId(), EventType.valueOf(se.getEventType()),
                se.getClassification(), se.getIntensity(), se.getEventDate(), se.getDescription());

        SpaceAssetEntity sa = ra.getSpaceAsset();
        OrganizationEntity org = sa.getOrganization();
        Organization domainOrg = new Organization(org.getId(), org.getName(), org.getType(),
                org.getContactEmail(), org.getCountry());
        SpaceAsset spaceAsset = new SpaceAsset(sa.getId(), sa.getName(), AssetType.valueOf(sa.getAssetType()),
                sa.getOrbit(), sa.getOperationalStatus(), domainOrg);

        RiskAnalysis riskAnalysis = new RiskAnalysis(ra.getId(), spaceEvent, spaceAsset,
                RiskLevel.valueOf(ra.getRiskLevel()), ra.getRiskScore(), ra.getAnalysisDate());

        return new Alert(e.getId(), riskAnalysis, e.getMessage(),
                e.getIssueDate(), AlertStatus.valueOf(e.getStatus()));
    }
}
