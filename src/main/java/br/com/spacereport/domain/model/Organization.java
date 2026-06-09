package br.com.spacereport.domain.model;

public class Organization {

    private final Long id;
    private final String name;
    private final String type;
    private final String contactEmail;
    private final String country;

    public Organization(Long id, String name, String type, String contactEmail, String country) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.contactEmail = contactEmail;
        this.country = country;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getType() { return type; }
    public String getContactEmail() { return contactEmail; }
    public String getCountry() { return country; }

    @Override
    public String toString() {
        return "Organization{id=" + id + ", name='" + name + "', type='" + type + "'}";
    }
}
