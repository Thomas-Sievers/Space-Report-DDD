package br.com.spacereport.domain.port.in;

import br.com.spacereport.domain.model.Alert;

import java.util.List;

public interface QueryAlertUseCase {
    List<Alert> findCritical();
    List<Alert> findAll();
}
