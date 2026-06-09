package br.com.spacereport.infrastructure.adapter.out;

import br.com.spacereport.domain.model.EventType;
import br.com.spacereport.domain.model.SpaceEvent;
import br.com.spacereport.domain.port.out.SpaceEventRepository;
import br.com.spacereport.infrastructure.adapter.out.persistence.entity.SpaceEventEntity;
import br.com.spacereport.infrastructure.adapter.out.persistence.repository.SpaceEventJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class SpaceEventRepositoryAdapter implements SpaceEventRepository {

    private final SpaceEventJpaRepository jpaRepository;

    public SpaceEventRepositoryAdapter(SpaceEventJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public SpaceEvent save(SpaceEvent spaceEvent) {
        return toDomain(jpaRepository.save(toEntity(spaceEvent)));
    }

    @Override
    public SpaceEvent update(SpaceEvent spaceEvent) {
        return toDomain(jpaRepository.save(toEntity(spaceEvent)));
    }

    @Override
    public void delete(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public Optional<SpaceEvent> findById(Long id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<SpaceEvent> findAll() {
        return jpaRepository.findAll().stream().map(this::toDomain).toList();
    }

    private SpaceEventEntity toEntity(SpaceEvent e) {
        SpaceEventEntity entity = new SpaceEventEntity();
        entity.setId(e.getId());
        entity.setEventType(e.getEventType().name());
        entity.setClassification(e.getClassification());
        entity.setIntensity(e.getIntensity());
        entity.setEventDate(e.getEventDate());
        entity.setDescription(e.getDescription());
        return entity;
    }

    private SpaceEvent toDomain(SpaceEventEntity e) {
        return new SpaceEvent(e.getId(), EventType.valueOf(e.getEventType()),
                e.getClassification(), e.getIntensity(), e.getEventDate(), e.getDescription());
    }
}
