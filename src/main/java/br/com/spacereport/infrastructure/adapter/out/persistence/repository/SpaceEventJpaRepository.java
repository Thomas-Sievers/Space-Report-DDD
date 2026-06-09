package br.com.spacereport.infrastructure.adapter.out.persistence.repository;

import br.com.spacereport.infrastructure.adapter.out.persistence.entity.SpaceEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpaceEventJpaRepository extends JpaRepository<SpaceEventEntity, Long> {}
