package br.com.spacereport.domain.port.out;

import br.com.spacereport.domain.model.Organization;

import java.util.List;
import java.util.Optional;

public interface OrganizationRepository {
    Organization save(Organization organization);
    Optional<Organization> findById(Long id);
    List<Organization> findAll();
}
