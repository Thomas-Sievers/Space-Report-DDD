package br.com.spacereport.infrastructure.adapter.out.persistence.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "Organizacao")
public class OrganizationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "IdOrganizacao")
    private Long id;

    @Column(name = "NomeOrganizacao", nullable = false, unique = true, length = 100)
    private String name;

    @Column(name = "TipoOrganizacao", nullable = false, length = 50)
    private String type;

    @Column(name = "EmailContato", nullable = false, length = 100)
    private String contactEmail;

    @Column(name = "Pais", length = 50)
    private String country;

    public OrganizationEntity() {}

    public OrganizationEntity(Long id, String name, String type, String contactEmail, String country) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.contactEmail = contactEmail;
        this.country = country;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getContactEmail() { return contactEmail; }
    public void setContactEmail(String contactEmail) { this.contactEmail = contactEmail; }
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
}
