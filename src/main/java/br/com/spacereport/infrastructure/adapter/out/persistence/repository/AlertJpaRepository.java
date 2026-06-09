package br.com.spacereport.infrastructure.adapter.out.persistence.repository;

import br.com.spacereport.infrastructure.adapter.out.persistence.entity.AlertEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlertJpaRepository extends JpaRepository<AlertEntity, Long> {
    List<AlertEntity> findByStatus(String status);
}
