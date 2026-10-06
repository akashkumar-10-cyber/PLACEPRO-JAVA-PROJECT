package com.placepro;

import com.placepro.dto.CompanyDTO;
import com.placepro.service.CompanyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class CompanyServiceTest {

    @Autowired
    private CompanyService companyService;

    @Test
    void testGetAllCompanies() {
        List<CompanyDTO> companies = companyService.getAllCompanies();
        assertNotNull(companies);
        assertTrue(companies.size() >= 5, "Expected at least 5 seeded companies.");
    }

    @Test
    void testCreateCompany() {
        CompanyDTO dto = new CompanyDTO();
        dto.setName("Test Corp");
        dto.setIndustry("Software");
        dto.setLocation("Bengaluru");
        dto.setMinimumCgpa(8.0);
        dto.setMaximumArrears(0);
        dto.setRequiredSkills("Java, Spring, Microservices");
        dto.setPackageOffered("8.0 - 15.0 LPA");
        dto.setDescription("Test company description");

        CompanyDTO created = companyService.createCompany(dto);
        assertNotNull(created.getId());
        assertEquals("Test Corp", created.getName());
    }
}
