package com.placepro;

import com.placepro.model.Company;
import com.placepro.model.PlacementDrive;
import com.placepro.model.Student;
import com.placepro.service.PlacementDriveService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class EligibilityTest {

    @Autowired
    private PlacementDriveService driveService;

    @Test
    void testEligibilityRules() {
        Company company = new Company();
        company.setMinimumCgpa(7.5);
        company.setMaximumArrears(0);

        PlacementDrive drive = new PlacementDrive();
        drive.setCompany(company);
        drive.setStatus("OPEN");
        drive.setApplicationDeadline(LocalDate.now().plusDays(10));

        // Eligible Student
        Student eligibleStudent = new Student();
        eligibleStudent.setCgpa(8.5);
        eligibleStudent.setArrears(0);

        Map<String, Object> result1 = driveService.checkEligibility(eligibleStudent, drive);
        assertTrue((Boolean) result1.get("eligible"));

        // Ineligible Student (Low CGPA)
        Student lowCgpaStudent = new Student();
        lowCgpaStudent.setCgpa(6.5);
        lowCgpaStudent.setArrears(0);

        Map<String, Object> result2 = driveService.checkEligibility(lowCgpaStudent, drive);
        assertFalse((Boolean) result2.get("eligible"));

        // Ineligible Student (Has Arrears)
        Student arrearsStudent = new Student();
        arrearsStudent.setCgpa(8.0);
        arrearsStudent.setArrears(2);

        Map<String, Object> result3 = driveService.checkEligibility(arrearsStudent, drive);
        assertFalse((Boolean) result3.get("eligible"));
    }
}
