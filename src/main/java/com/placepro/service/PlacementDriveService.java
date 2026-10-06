package com.placepro.service;

import com.placepro.exception.ResourceNotFoundException;
import com.placepro.model.Company;
import com.placepro.model.PlacementDrive;
import com.placepro.model.Student;
import com.placepro.repository.CompanyRepository;
import com.placepro.repository.PlacementDriveRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PlacementDriveService {

    private final PlacementDriveRepository driveRepository;
    private final CompanyRepository companyRepository;

    public PlacementDriveService(PlacementDriveRepository driveRepository, CompanyRepository companyRepository) {
        this.driveRepository = driveRepository;
        this.companyRepository = companyRepository;
    }

    public List<PlacementDrive> getAllDrives() {
        return driveRepository.findAll();
    }

    public PlacementDrive getDriveById(Long id) {
        return driveRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Placement drive not found with id: " + id));
    }

    public List<PlacementDrive> getActiveDrives() {
        return driveRepository.findByStatus("OPEN");
    }

    @Transactional
    public PlacementDrive createDrive(Long companyId, LocalDate driveDate, LocalDate applicationDeadline,
                                       String jobRole, String eligibilityDescription) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + companyId));

        PlacementDrive drive = new PlacementDrive();
        drive.setCompany(company);
        drive.setDriveDate(driveDate);
        drive.setApplicationDeadline(applicationDeadline);
        drive.setJobRole(jobRole);
        drive.setEligibilityDescription(eligibilityDescription);
        drive.setStatus("OPEN");

        return driveRepository.save(drive);
    }

    @Transactional
    public PlacementDrive updateDrive(Long id, Long companyId, LocalDate driveDate, LocalDate applicationDeadline,
                                       String jobRole, String eligibilityDescription, String status) {
        PlacementDrive drive = getDriveById(id);
        if (companyId != null) {
            Company company = companyRepository.findById(companyId)
                    .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + companyId));
            drive.setCompany(company);
        }
        drive.setDriveDate(driveDate);
        drive.setApplicationDeadline(applicationDeadline);
        drive.setJobRole(jobRole);
        drive.setEligibilityDescription(eligibilityDescription);
        drive.setStatus(status);

        return driveRepository.save(drive);
    }

    @Transactional
    public PlacementDrive closeDrive(Long id) {
        PlacementDrive drive = getDriveById(id);
        drive.setStatus("CLOSED");
        return driveRepository.save(drive);
    }

    public long countActiveDrives() {
        return driveRepository.countByStatus("OPEN");
    }

    public Map<String, Object> checkEligibility(Student student, PlacementDrive drive) {
        Map<String, Object> result = new HashMap<>();
        Company company = drive.getCompany();
        List<String> reasons = new ArrayList<>();
        boolean isEligible = true;

        if (drive.getApplicationDeadline() != null && LocalDate.now().isAfter(drive.getApplicationDeadline())) {
            isEligible = false;
            reasons.add("Application deadline has passed (" + drive.getApplicationDeadline() + ").");
        }

        if (!"OPEN".equalsIgnoreCase(drive.getStatus())) {
            isEligible = false;
            reasons.add("Placement drive is " + drive.getStatus() + ".");
        }

        if (company.getMinimumCgpa() != null && student.getCgpa() != null && student.getCgpa() < company.getMinimumCgpa()) {
            isEligible = false;
            reasons.add(String.format("Student CGPA (%.2f) is less than company minimum CGPA (%.2f).",
                    student.getCgpa(), company.getMinimumCgpa()));
        }

        if (company.getMaximumArrears() != null && student.getArrears() != null && student.getArrears() > company.getMaximumArrears()) {
            isEligible = false;
            reasons.add(String.format("Student arrears (%d) exceeds company maximum allowed arrears (%d).",
                    student.getArrears(), company.getMaximumArrears()));
        }

        result.put("eligible", isEligible);
        result.put("reasons", reasons);
        return result;
    }
}
