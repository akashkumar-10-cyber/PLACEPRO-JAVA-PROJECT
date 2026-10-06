package com.placepro.dto;

import jakarta.validation.constraints.*;

public class CompanyDTO {

    private Long id;

    @NotBlank(message = "Company Name is required")
    private String name;

    @NotBlank(message = "Industry is required")
    private String industry;

    @NotBlank(message = "Location is required")
    private String location;

    @NotNull(message = "Minimum CGPA is required")
    @DecimalMin(value = "6.5", message = "Minimum CGPA must be at least 6.5")
    @DecimalMax(value = "10.0", message = "Minimum CGPA cannot exceed 10.0")
    private Double minimumCgpa;

    private Integer maximumArrears = 0;

    @NotBlank(message = "Required skills are required")
    private String requiredSkills;

    @NotBlank(message = "Package offered details are required")
    private String packageOffered;

    private String description;

    public CompanyDTO() {}

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
