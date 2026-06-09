package br.com.spacereport.domain.port.out;

import br.com.spacereport.domain.model.Alert;

public interface AlertNotifier {
    void notify(Alert alert);
}
