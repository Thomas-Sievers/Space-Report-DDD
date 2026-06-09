package br.com.spacereport.domain.port.out;

import br.com.spacereport.domain.model.SpaceEvent;

import java.util.List;
import java.util.Optional;

public interface SpaceEventRepository {
    SpaceEvent save(SpaceEvent spaceEvent);
    SpaceEvent update(SpaceEvent spaceEvent);
    void delete(Long id);
    Optional<SpaceEvent> findById(Long id);
    List<SpaceEvent> findAll();
}
