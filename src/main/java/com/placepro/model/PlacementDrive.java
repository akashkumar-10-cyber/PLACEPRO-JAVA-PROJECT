package com.placepro.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "placement_drives")
public class PlacementDrive {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    private LocalDate driveDate;
    private LocalDate applicationDeadline;
    private String jobRole;

    @Column(length = 1000)
    private String eligibilityDescription;

    private String status; // OPEN, CLOSED, COMPLETED

    public PlacementDrive() {}

    public PlacementDrive(Company company, String jobRole, LocalDate driveDate, LocalDate applicationDeadline, String status, String eligibilityDescription) {
        this.company = company;
        this.jobRole = jobRole;
        this.driveDate = driveDate;
        this.applicationDeadline = applicationDeadline;
        this.status = status;
        this.eligibilityDescription = eligibilityDescription;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Company getCompany() { return company; }
    public void setCompany(Company company) { this.company = company; }

    public LocalDate getDriveDate() { return driveDate; }
    public void setDriveDate(LocalDate driveDate) { this.driveDate = driveDate; }

    public LocalDate getApplicationDeadline() { return applicationDeadline; }
    public void setApplicationDeadline(LocalDate applicationDeadline) { this.applicationDeadline = applicationDeadline; }

    public String getJobRole() { return jobRole; }
    public void setJobRole(String jobRole) { this.jobRole = jobRole; }

    public String getEligibilityDescription() { return eligibilityDescription; }
    public void setEligibilityDescription(String eligibilityDescription) { this.eligibilityDescription = eligibilityDescription; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
