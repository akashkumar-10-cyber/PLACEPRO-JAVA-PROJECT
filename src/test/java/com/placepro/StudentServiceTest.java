package com.placepro;

import com.placepro.dto.StudentDTO;
import com.placepro.service.StudentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class StudentServiceTest {

    @Autowired
    private StudentService studentService;

    @Test
    void testGetAllStudents() {
        List<StudentDTO> students = studentService.getAllStudents();
        assertNotNull(students);
        assertTrue(students.size() >= 8, "Expected at least 8 seeded students.");
    }

    @Test
    void testCreateAndSearchStudent() {
        StudentDTO dto = new StudentDTO();
        dto.setRegistrationNumber("TEST2026001");
        dto.setName("Test Student");
        dto.setEmail("test.student@placepro.edu");
        dto.setDepartment("Computer Science");
        dto.setGraduationYear(2026);
        dto.setCgpa(9.5);
        dto.setArrears(0);
        dto.setSkills("Java, Spring Boot, SQL");
        dto.setAptitudeScore(90.0);
        dto.setCommunicationScore(92.0);

        StudentDTO created = studentService.createStudent(dto, "admin1");
        assertNotNull(created.getId());
        assertEquals("Test Student", created.getName());

        List<StudentDTO> searchResults = studentService.searchStudents("Test Student", null, null);
        assertFalse(searchResults.isEmpty());
    }
}
