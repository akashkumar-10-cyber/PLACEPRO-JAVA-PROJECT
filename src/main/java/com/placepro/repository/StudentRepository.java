package com.placepro.repository;

import com.placepro.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findByRegistrationNumber(String registrationNumber);

    Optional<Student> findByEmail(String email);

    Optional<Student> findByUserId(Long userId);

    Optional<Student> findByUserUsername(String username);

    List<Student> findByAssignedAdminId(Long assignedAdminId);

    List<Student> findByAssignedAdminUsername(String username);

    @Query("SELECT s FROM Student s WHERE " +
           "(:query IS NULL OR LOWER(s.name) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(s.registrationNumber) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(s.department) LIKE LOWER(CONCAT('%', :query, '%'))) AND " +
           "(:dept IS NULL OR s.department = :dept) AND " +
           "(:minCgpa IS NULL OR s.cgpa >= :minCgpa) AND " +
           "(:adminId IS NULL OR s.assignedAdmin.id = :adminId)")
    List<Student> searchStudents(@Param("query") String query,
                                @Param("dept") String dept,
                                @Param("minCgpa") Double minCgpa,
                                @Param("adminId") Long adminId);

    @Query("SELECT s.department, COUNT(s) FROM Student s GROUP BY s.department")
    List<Object[]> countStudentsByDepartment();
}
