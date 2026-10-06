package com.placepro;

import com.placepro.model.Application;
import com.placepro.model.PlacementDrive;
import com.placepro.model.Student;
import com.placepro.repository.PlacementDriveRepository;
import com.placepro.repository.StudentRepository;
import com.placepro.service.ApplicationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ApplicationServiceTest {

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private PlacementDriveRepository driveRepository;

    @Test
    void testDuplicateApplicationPrevention() {
        List<Student> students = studentRepository.findAll();
        List<PlacementDrive> drives = driveRepository.findAll();

        assertFalse(students.isEmpty());
        assertFalse(drives.isEmpty());

        Student student = students.get(0);
        PlacementDrive drive = drives.get(0);

        List<Application> existing = applicationService.getApplicationsByStudent(student);
        boolean alreadyApplied = existing.stream().anyMatch(a -> a.getPlacementDrive().getId().equals(drive.getId()));

        if (!alreadyApplied) {
            try {
                applicationService.applyForDrive(student, drive);
            } catch (Exception ignored) {}
        }

        assertThrows(IllegalArgumentException.class, () -> {
            applicationService.applyForDrive(student, drive);
        });
    }
}
