package com.placepro;

import com.placepro.dto.CompanyDTO;
import com.placepro.dto.RecommendationDTO;
import com.placepro.dto.StudentDTO;
import com.placepro.service.AIRecommendationService;
import com.placepro.service.CompanyService;
import com.placepro.service.StudentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class AIRecommendationTest {

    @Autowired
    private AIRecommendationService aiService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private CompanyService companyService;

    @Test
    void testLocalRecommendationEngineFallback() {
        List<StudentDTO> students = studentService.getAllStudents();
        List<CompanyDTO> companies = companyService.getAllCompanies();

        assertFalse(students.isEmpty());
        assertFalse(companies.isEmpty());

        StudentDTO student = students.get(0);
        List<RecommendationDTO> recommendations = aiService.getCompanyRecommendations(student.getId());

        assertNotNull(recommendations);
        assertFalse(recommendations.isEmpty());

        RecommendationDTO topRec = recommendations.get(0);
        assertNotNull(topRec.getMatchScore());
        assertTrue(topRec.getMatchScore() >= 0.0 && topRec.getMatchScore() <= 100.0);
        assertNotNull(topRec.getSource());
    }

    @Test
    void testPlacementReadinessAssessment() {
        List<StudentDTO> students = studentService.getAllStudents();
        StudentDTO student = students.get(0);

        Map<String, Object> readiness = aiService.assessPlacementReadiness(student.getId());
        assertNotNull(readiness);
        assertNotNull(readiness.get("readinessLevel"));
        assertNotNull(readiness.get("overallScore"));
        assertNotNull(readiness.get("disclaimer"));
    }
}
