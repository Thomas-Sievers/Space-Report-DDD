package br.com.spacereport.infrastructure.adapter.out;

import br.com.spacereport.domain.model.Organization;
import br.com.spacereport.domain.port.out.OrganizationRepository;
import br.com.spacereport.infrastructure.adapter.out.persistence.entity.OrganizationEntity;
import br.com.spacereport.infrastructure.adapter.out.persistence.repository.OrganizationJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class OrganizationRepositoryAdapter implements OrganizationRepository {

    private final OrganizationJpaRepository jpaRepository;

    public OrganizationRepositoryAdapter(OrganizationJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Organization save(Organization organization) {
        OrganizationEntity entity = toEntity(organization);
        OrganizationEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Organization update(Organization organization) {
        OrganizationEntity entity = toEntity(organization);
        return toDomain(jpaRepository.save(entity));
    }

    @Override
    public void delete(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public Optional<Organization> findById(Long id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Organization> findAll() {
        return jpaRepository.findAll().stream().map(this::toDomain).toList();
    }

    private OrganizationEntity toEntity(Organization o) {
        return new OrganizationEntity(o.getId(), o.getName(), o.getType(), o.getContactEmail(), o.getCountry());
    }

    private Organization toDomain(OrganizationEntity e) {
        return new Organization(e.getId(), e.getName(), e.getType(), e.getContactEmail(), e.getCountry());
    }
}
