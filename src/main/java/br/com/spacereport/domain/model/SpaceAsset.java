package br.com.spacereport.domain.model;

public class SpaceAsset {

    private final Long id;
    private final String name;
    private final AssetType assetType;
    private final String orbit;
    private final String operationalStatus;
    private final Organization organization;

    public SpaceAsset(Long id, String name, AssetType assetType, String orbit,
                      String operationalStatus, Organization organization) {
        this.id = id;
        this.name = name;
        this.assetType = assetType;
        this.orbit = orbit;
        this.operationalStatus = operationalStatus;
        this.organization = organization;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public AssetType getAssetType() { return assetType; }
    public String getOrbit() { return orbit; }
    public String getOperationalStatus() { return operationalStatus; }
    public Organization getOrganization() { return organization; }

    @Override
    public String toString() {
        return "SpaceAsset{id=" + id + ", name='" + name + "', orbit='" + orbit + "'}";
    }
}
