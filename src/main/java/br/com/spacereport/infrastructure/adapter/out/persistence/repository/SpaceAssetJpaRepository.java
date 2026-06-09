package br.com.spacereport.infrastructure.adapter.out.persistence.repository;

import br.com.spacereport.infrastructure.adapter.out.persistence.entity.SpaceAssetEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpaceAssetJpaRepository extends JpaRepository<SpaceAssetEntity, Long> {
    List<SpaceAssetEntity> findByOrganizationId(Long organizationId);
}
