package com.placepro.repository;

import com.placepro.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CompanyRepository extends JpaRepository<Company, Long> {

    @Query("SELECT c FROM Company c WHERE " +
           "(:query IS NULL OR LOWER(c.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(c.industry) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(c.requiredSkills) LIKE LOWER(CONCAT('%', :query, '%')))")
    List<Company> searchCompanies(@Param("query") String query);
}
