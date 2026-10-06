package com.placepro.repository;

import com.placepro.model.Company;
import com.placepro.model.Recommendation;
import com.placepro.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {
    List<Recommendation> findByStudentOrderByMatchScoreDesc(Student student);
    Optional<Recommendation> findByStudentAndCompany(Student student, Company company);
    void deleteByStudent(Student student);
}
