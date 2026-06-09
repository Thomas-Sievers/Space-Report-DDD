package br.com.spacereport.infrastructure.adapter.out.persistence.repository;

import br.com.spacereport.infrastructure.adapter.out.persistence.entity.OrganizationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrganizationJpaRepository extends JpaRepository<OrganizationEntity, Long> {}
