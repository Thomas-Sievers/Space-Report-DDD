package br.com.spacereport.domain.model;

public abstract class DomainEntity {

    protected final Long id;

    protected DomainEntity(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
