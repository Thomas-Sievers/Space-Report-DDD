package br.com.spacereport.domain.port.out;

import br.com.spacereport.domain.model.Alert;
import br.com.spacereport.domain.model.AlertStatus;

import java.util.List;
import java.util.Optional;

public interface AlertRepository {
    Alert save(Alert alert);
    Alert update(Alert alert);
    void delete(Long id);
    Optional<Alert> findById(Long id);
    List<Alert> findByStatus(AlertStatus status);
    List<Alert> findAll();
}
