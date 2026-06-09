package br.com.spacereport.domain.port.in;

import br.com.spacereport.domain.model.SpaceEvent;

import java.time.LocalDate;
import java.util.List;

public interface IngestSpaceEventUseCase {
    void ingest(SpaceEvent spaceEvent);
    List<SpaceEvent> ingestFromApi(LocalDate startDate, LocalDate endDate);
}
