package com.placepro.service;

import com.placepro.exception.ResourceNotFoundException;
import com.placepro.model.Application;
import com.placepro.model.PlacementDrive;
import com.placepro.model.Student;
import com.placepro.repository.ApplicationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final PlacementDriveService driveService;

    public ApplicationService(ApplicationRepository applicationRepository, PlacementDriveService driveService) {
        this.applicationRepository = applicationRepository;
        this.driveService = driveService;
    }

    public List<Application> getAllApplications() {
        return applicationRepository.findAll();
    }

    public Application getApplicationById(Long id) {
        return applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found with id: " + id));
    }

    public List<Application> getApplicationsByStudent(Student student) {
        return applicationRepository.findByStudent(student);
    }

    public List<Application> getApplicationsByDrive(PlacementDrive drive) {
        return applicationRepository.findByPlacementDrive(drive);
    }

    public List<Application> filterApplications(Long companyId, String status, String dept) {
        Long cleanCompanyId = (companyId != null && companyId > 0) ? companyId : null;
        String cleanStatus = (status != null && !status.trim().isEmpty()) ? status.trim() : null;
        String cleanDept = (dept != null && !dept.trim().isEmpty()) ? dept.trim() : null;
        return applicationRepository.filterApplications(cleanCompanyId, cleanStatus, cleanDept);
    }

    @Transactional
    public Application applyForDrive(Student student, PlacementDrive drive) {
        // Prevent duplicates
        if (applicationRepository.existsByStudentAndPlacementDrive(student, drive)) {
            throw new IllegalArgumentException("You have already applied for this placement drive.");
        }

        // Verify eligibility
        Map<String, Object> eligibility = driveService.checkEligibility(student, drive);
        if (!Boolean.TRUE.equals(eligibility.get("eligible"))) {
            throw new IllegalArgumentException("Student is not eligible for this drive: " + eligibility.get("reasons"));
        }

        Application application = new Application();
        application.setStudent(student);
        application.setPlacementDrive(drive);
        application.setAppliedDate(LocalDate.now());
        application.setStatus("APPLIED");

        return applicationRepository.save(application);
    }

    @Transactional
    public Application updateApplicationStatus(Long applicationId, String status) {
        Application application = getApplicationById(applicationId);
        application.setStatus(status);
        return applicationRepository.save(application);
    }

    public long countTotalApplications() {
        return applicationRepository.count();
    }

    public long countPlacedStudents() {
        return applicationRepository.countByStatus("SELECTED");
    }
}
