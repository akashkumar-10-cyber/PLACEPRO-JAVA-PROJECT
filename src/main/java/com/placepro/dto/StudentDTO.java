package com.placepro.dto;

import jakarta.validation.constraints.*;

public class StudentDTO {

    private Long id;

    @NotBlank(message = "Registration number is required")
    private String registrationNumber;

    @NotBlank(message = "Student name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    private String phone;

    @NotBlank(message = "Department is required")
    private String department;

    @NotNull(message = "CGPA is required")
    @DecimalMin(value = "6.5", message = "CGPA must be at least 6.5")
    @DecimalMax(value = "10.0", message = "CGPA cannot exceed 10.0")
    private Double cgpa;

    @NotNull(message = "Graduation year is required")
    private Integer graduationYear;

    @NotNull(message = "Arrears count is required")
    @Min(value = 0, message = "Arrears cannot be negative")
    private Integer arrears = 0;

    private String skills;
    private String certifications;
    private String projects;
    private Double aptitudeScore;
    private Double communicationScore;

    private String password;

    private Long assignedAdminId;
    private String assignedAdminUsername;

    public StudentDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getRegistrationNumber() { return registrationNumber; }
    public void setRegistrationNumber(String registrationNumber) { this.registrationNumber = registrationNumber; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public Double getCgpa() { return cgpa; }
    public void setCgpa(Double cgpa) { this.cgpa = cgpa; }

    public Integer getGraduationYear() { return graduationYear; }
    public void setGraduationYear(Integer graduationYear) { this.graduationYear = graduationYear; }

    public Integer getArrears() { return arrears; }
    public void setArrears(Integer arrears) { this.arrears = arrears; }

    public String getSkills() { return skills; }
    public void setSkills(String skills) { this.skills = skills; }

    public String getCertifications() { return certifications; }
    public void setCertifications(String certifications) { this.certifications = certifications; }

    public String getProjects() { return projects; }
    public void setProjects(String projects) { this.projects = projects; }

    public Double getAptitudeScore() { return aptitudeScore; }
    public void setAptitudeScore(Double aptitudeScore) { this.aptitudeScore = aptitudeScore; }

    public Double getCommunicationScore() { return communicationScore; }
    public void setCommunicationScore(Double communicationScore) { this.communicationScore = communicationScore; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Long getAssignedAdminId() { return assignedAdminId; }
    public void setAssignedAdminId(Long assignedAdminId) { this.assignedAdminId = assignedAdminId; }

    public String getAssignedAdminUsername() { return assignedAdminUsername; }
    public void setAssignedAdminUsername(String assignedAdminUsername) { this.assignedAdminUsername = assignedAdminUsername; }
}
