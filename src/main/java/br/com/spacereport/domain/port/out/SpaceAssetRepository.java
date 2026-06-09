package br.com.spacereport.domain.port.out;

import br.com.spacereport.domain.model.Organization;
import br.com.spacereport.domain.model.SpaceAsset;

import java.util.List;
import java.util.Optional;

public interface SpaceAssetRepository {
    SpaceAsset save(SpaceAsset spaceAsset);
    Optional<SpaceAsset> findById(Long id);
    List<SpaceAsset> findByOrganization(Organization organization);
    List<SpaceAsset> findAll();
}
