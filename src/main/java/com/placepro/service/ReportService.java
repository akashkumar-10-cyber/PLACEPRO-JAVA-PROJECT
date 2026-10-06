package com.placepro.service;

import com.placepro.model.Application;
import com.placepro.repository.ApplicationRepository;
import com.placepro.repository.CompanyRepository;
import com.placepro.repository.PlacementDriveRepository;
import com.placepro.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.*;

@Service
public class ReportService {

    private final StudentRepository studentRepository;
    private final CompanyRepository companyRepository;
    private final PlacementDriveRepository driveRepository;
    private final ApplicationRepository applicationRepository;

    public ReportService(StudentRepository studentRepository,
                         CompanyRepository companyRepository,
                         PlacementDriveRepository driveRepository,
                         ApplicationRepository applicationRepository) {
        this.studentRepository = studentRepository;
        this.companyRepository = companyRepository;
        this.driveRepository = driveRepository;
        this.applicationRepository = applicationRepository;
    }

    public Map<String, Object> getOverallStatistics() {
        long totalStudents = studentRepository.count();
        long totalCompanies = companyRepository.count();
        long activeDrives = driveRepository.countByStatus("OPEN");
        long totalApplications = applicationRepository.count();
        long selectedStudents = applicationRepository.countByStatus("SELECTED");
        long rejectedStudents = applicationRepository.countByStatus("REJECTED");

        double placementPercentage = totalStudents > 0
                ? Math.round(((double) selectedStudents / totalStudents) * 100.0 * 10.0) / 10.0
                : 0.0;

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalStudents", totalStudents);
        stats.put("totalCompanies", totalCompanies);
        stats.put("activeDrives", activeDrives);
        stats.put("totalApplications", totalApplications);
        stats.put("selectedStudents", selectedStudents);
        stats.put("rejectedStudents", rejectedStudents);
        stats.put("placementPercentage", placementPercentage);

        // Chart Data
        List<Object[]> deptCounts = studentRepository.countStudentsByDepartment();
        Map<String, Long> deptChart = new LinkedHashMap<>();
        for (Object[] row : deptCounts) {
            deptChart.put((String) row[0], (Long) row[1]);
        }
        stats.put("departmentWiseStudents", deptChart);

        List<Object[]> companyCounts = applicationRepository.countApplicationsByCompany();
        Map<String, Long> companyChart = new LinkedHashMap<>();
        for (Object[] row : companyCounts) {
            companyChart.put((String) row[0], (Long) row[1]);
        }
        stats.put("companyWiseApplications", companyChart);

        List<Object[]> statusCounts = applicationRepository.countApplicationsByStatus();
        Map<String, Long> statusChart = new LinkedHashMap<>();
        for (Object[] row : statusCounts) {
            statusChart.put((String) row[0], (Long) row[1]);
        }
        stats.put("statusWiseApplications", statusChart);

        return stats;
    }

    public ByteArrayInputStream generateApplicationsCsv() {
        List<Application> applications = applicationRepository.findAll();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(out);

        // CSV Header
        writer.println("Application ID,Reg Number,Student Name,Department,CGPA,Company,Job Role,Applied Date,Status");

        for (Application app : applications) {
            writer.println(String.format("%d,\"%s\",\"%s\",\"%s\",%.2f,\"%s\",\"%s\",%s,%s",
                    app.getId(),
                    app.getStudent().getRegistrationNumber(),
                    app.getStudent().getName(),
                    app.getStudent().getDepartment(),
                    app.getStudent().getCgpa(),
                    app.getPlacementDrive().getCompany().getName(),
                    app.getPlacementDrive().getJobRole(),
                    app.getAppliedDate(),
                    app.getStatus()
            ));
        }

        writer.flush();
        return new ByteArrayInputStream(out.toByteArray());
    }
}
