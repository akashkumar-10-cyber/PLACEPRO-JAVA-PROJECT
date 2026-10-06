package com.placepro.model;

import jakarta.persistence.*;

@Entity
@Table(name = "companies")
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String industry;
    private String location;
    private Double minimumCgpa;
    private Integer maximumArrears;

    @Column(length = 1000)
    private String requiredSkills;

    @Column(name = "package_offered", length = 500)
    private String packageOffered; // e.g. "7.0 - 12.5 LPA" or "8.5 LPA, 10.0 LPA"

    @Column(length = 1500)
    private String description;

    public Company() {}

    public Company(String name, String industry, String location, String packageOffered, Double minimumCgpa, Integer maximumArrears, String requiredSkills, String description) {
        this.name = name;
        this.industry = industry;
        this.location = location;
        this.packageOffered = packageOffered;
        this.minimumCgpa = minimumCgpa;
        this.maximumArrears = maximumArrears;
        this.requiredSkills = requiredSkills;
        this.description = description;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getIndustry() { return industry; }
    public void setIndustry(String industry) { this.industry = industry; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public Double getMinimumCgpa() { return minimumCgpa; }
    public void setMinimumCgpa(Double minimumCgpa) { this.minimumCgpa = minimumCgpa; }

    public Integer getMaximumArrears() { return maximumArrears; }
    public void setMaximumArrears(Integer maximumArrears) { this.maximumArrears = maximumArrears; }

    public String getRequiredSkills() { return requiredSkills; }
    public void setRequiredSkills(String requiredSkills) { this.requiredSkills = requiredSkills; }

    public String getPackageOffered() { return packageOffered; }
    public void setPackageOffered(String packageOffered) { this.packageOffered = packageOffered; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
