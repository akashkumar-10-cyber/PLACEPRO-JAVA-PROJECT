package com.placepro.model;

import jakarta.persistence.*;

@Entity
@Table(name = "students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String registrationNumber;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    private String phone;
    private String department;
    private Integer graduationYear;
    private Double cgpa;
    private Integer arrears;

    @Column(length = 1000)
    private String skills;

    @Column(length = 1000)
    private String certifications;

    @Column(length = 1000)
    private String projects;

    private Double aptitudeScore;
    private Double communicationScore;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "assigned_admin_id")
    private User assignedAdmin;

    public Student() {}

    public Student(String registrationNumber, String name, String email, String phone,
                   String department, Integer graduationYear, Double cgpa, Integer arrears,
                   String skills, String certifications, String projects,
                   Double aptitudeScore, Double communicationScore, User user) {
        this.registrationNumber = registrationNumber;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.department = department;
        this.graduationYear = graduationYear;
        this.cgpa = cgpa;
        this.arrears = arrears;
        this.skills = skills;
        this.certifications = certifications;
        this.projects = projects;
        this.aptitudeScore = aptitudeScore;
        this.communicationScore = communicationScore;
        this.user = user;
    }

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

    public Integer getGraduationYear() { return graduationYear; }
    public void setGraduationYear(Integer graduationYear) { this.graduationYear = graduationYear; }

    public Double getCgpa() { return cgpa; }
    public void setCgpa(Double cgpa) { this.cgpa = cgpa; }

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

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public User getAssignedAdmin() { return assignedAdmin; }
    public void setAssignedAdmin(User assignedAdmin) { this.assignedAdmin = assignedAdmin; }
}
