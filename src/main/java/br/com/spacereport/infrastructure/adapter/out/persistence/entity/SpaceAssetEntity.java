package br.com.spacereport.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "AtivoEspacial")
public class SpaceAssetEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "IdAtivo")
    private Long id;

    @Column(name = "NomeAtivo", nullable = false, length = 100)
    private String name;

    @Column(name = "TipoAtivo", nullable = false, length = 50)
    private String assetType;

    @Column(name = "Orbita", length = 30)
    private String orbit;

    @Column(name = "StatusOperacional", nullable = false, length = 30)
    private String operationalStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "IdOrganizacao", nullable = false)
    private OrganizationEntity organization;

    public SpaceAssetEntity() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getAssetType() { return assetType; }
    public void setAssetType(String assetType) { this.assetType = assetType; }
    public String getOrbit() { return orbit; }
    public void setOrbit(String orbit) { this.orbit = orbit; }
    public String getOperationalStatus() { return operationalStatus; }
    public void setOperationalStatus(String operationalStatus) { this.operationalStatus = operationalStatus; }
    public OrganizationEntity getOrganization() { return organization; }
    public void setOrganization(OrganizationEntity organization) { this.organization = organization; }
}
