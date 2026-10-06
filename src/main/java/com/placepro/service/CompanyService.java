package com.placepro.service;

import com.placepro.dto.CompanyDTO;
import com.placepro.exception.ResourceNotFoundException;
import com.placepro.model.Company;
import com.placepro.repository.CompanyRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;

    public CompanyService(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    public List<CompanyDTO> getAllCompanies() {
        return companyRepository.findAll().stream()
                .map(this::convertToDto)
                .toList();
    }

    public CompanyDTO getCompanyById(Long id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + id));
        return convertToDto(company);
    }

    public List<CompanyDTO> searchCompanies(String query) {
        String cleanQuery = (query != null && !query.trim().isEmpty()) ? query.trim() : null;
        return companyRepository.searchCompanies(cleanQuery).stream()
                .map(this::convertToDto)
                .toList();
    }

    @Transactional
    public CompanyDTO createCompany(CompanyDTO dto) {
        Company company = convertToEntity(dto);
        Company saved = companyRepository.save(company);
        return convertToDto(saved);
    }

    @Transactional
    public CompanyDTO updateCompany(Long id, CompanyDTO dto) {
        Company existing = companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + id));

        existing.setName(dto.getName());
        existing.setIndustry(dto.getIndustry());
        existing.setLocation(dto.getLocation());
        existing.setMinimumCgpa(dto.getMinimumCgpa());
        existing.setMaximumArrears(dto.getMaximumArrears());
        existing.setRequiredSkills(dto.getRequiredSkills());
        existing.setPackageOffered(dto.getPackageOffered());
        existing.setDescription(dto.getDescription());

        Company updated = companyRepository.save(existing);
        return convertToDto(updated);
    }

    @Transactional
    public void deleteCompany(Long id) {
        Company company = companyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + id));
        companyRepository.delete(company);
    }

    public CompanyDTO convertToDto(Company company) {
        CompanyDTO dto = new CompanyDTO();
        dto.setId(company.getId());
        dto.setName(company.getName());
        dto.setIndustry(company.getIndustry());
        dto.setLocation(company.getLocation());
        dto.setMinimumCgpa(company.getMinimumCgpa());
        dto.setMaximumArrears(company.getMaximumArrears());
        dto.setRequiredSkills(company.getRequiredSkills());
        dto.setPackageOffered(company.getPackageOffered());
        dto.setDescription(company.getDescription());
        return dto;
    }

    public Company convertToEntity(CompanyDTO dto) {
        Company company = new Company();
        company.setId(dto.getId());
        company.setName(dto.getName());
        company.setIndustry(dto.getIndustry());
        company.setLocation(dto.getLocation());
        company.setMinimumCgpa(dto.getMinimumCgpa());
        company.setMaximumArrears(dto.getMaximumArrears() != null ? dto.getMaximumArrears() : 0);
        company.setRequiredSkills(dto.getRequiredSkills());
        company.setPackageOffered(dto.getPackageOffered());
        company.setDescription(dto.getDescription());
        return company;
    }
}
