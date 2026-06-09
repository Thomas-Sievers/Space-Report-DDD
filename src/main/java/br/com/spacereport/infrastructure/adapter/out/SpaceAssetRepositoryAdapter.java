package br.com.spacereport.infrastructure.adapter.out;

import br.com.spacereport.domain.model.AssetType;
import br.com.spacereport.domain.model.Organization;
import br.com.spacereport.domain.model.SpaceAsset;
import br.com.spacereport.domain.port.out.SpaceAssetRepository;
import br.com.spacereport.infrastructure.adapter.out.persistence.entity.OrganizationEntity;
import br.com.spacereport.infrastructure.adapter.out.persistence.entity.SpaceAssetEntity;
import br.com.spacereport.infrastructure.adapter.out.persistence.repository.OrganizationJpaRepository;
import br.com.spacereport.infrastructure.adapter.out.persistence.repository.SpaceAssetJpaRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
@Transactional
public class SpaceAssetRepositoryAdapter implements SpaceAssetRepository {

    private final SpaceAssetJpaRepository jpaRepository;
    private final OrganizationJpaRepository organizationJpaRepository;

    public SpaceAssetRepositoryAdapter(SpaceAssetJpaRepository jpaRepository,
                                       OrganizationJpaRepository organizationJpaRepository) {
        this.jpaRepository = jpaRepository;
        this.organizationJpaRepository = organizationJpaRepository;
    }

    @Override
    public SpaceAsset save(SpaceAsset spaceAsset) {
        SpaceAssetEntity entity = toEntity(spaceAsset);
        return toDomain(jpaRepository.save(entity));
    }

    @Override
    public Optional<SpaceAsset> findById(Long id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<SpaceAsset> findByOrganization(Organization organization) {
        return jpaRepository.findByOrganizationId(organization.getId())
                .stream().map(this::toDomain).toList();
    }

    @Override
    public List<SpaceAsset> findAll() {
        return jpaRepository.findAll().stream().map(this::toDomain).toList();
    }

    private SpaceAssetEntity toEntity(SpaceAsset a) {
        SpaceAssetEntity entity = new SpaceAssetEntity();
        entity.setId(a.getId());
        entity.setName(a.getName());
        entity.setAssetType(a.getAssetType().name());
        entity.setOrbit(a.getOrbit());
        entity.setOperationalStatus(a.getOperationalStatus());
        OrganizationEntity orgEntity = organizationJpaRepository
                .findById(a.getOrganization().getId())
                .orElseThrow(() -> new IllegalStateException("Organization not found: " + a.getOrganization().getId()));
        entity.setOrganization(orgEntity);
        return entity;
    }

    private SpaceAsset toDomain(SpaceAssetEntity e) {
        OrganizationEntity org = e.getOrganization();
        Organization domainOrg = new Organization(org.getId(), org.getName(), org.getType(),
                org.getContactEmail(), org.getCountry());
        return new SpaceAsset(e.getId(), e.getName(), AssetType.valueOf(e.getAssetType()),
                e.getOrbit(), e.getOperationalStatus(), domainOrg);
    }
}
