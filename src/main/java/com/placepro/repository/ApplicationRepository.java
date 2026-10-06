package com.placepro.repository;

import com.placepro.model.Application;
import com.placepro.model.PlacementDrive;
import com.placepro.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
    List<Application> findByStudent(Student student);
    List<Application> findByPlacementDrive(PlacementDrive drive);
    Optional<Application> findByStudentAndPlacementDrive(Student student, PlacementDrive drive);
    boolean existsByStudentAndPlacementDrive(Student student, PlacementDrive drive);
    long countByStatus(String status);

    @Query("SELECT a.status, COUNT(a) FROM Application a GROUP BY a.status")
    List<Object[]> countApplicationsByStatus();

    @Query("SELECT a.placementDrive.company.name, COUNT(a) FROM Application a GROUP BY a.placementDrive.company.name")
    List<Object[]> countApplicationsByCompany();

    @Query("SELECT a FROM Application a WHERE " +
           "(:companyId IS NULL OR a.placementDrive.company.id = :companyId) AND " +
           "(:status IS NULL OR a.status = :status) AND " +
           "(:dept IS NULL OR LOWER(a.student.department) = LOWER(:dept))")
    List<Application> filterApplications(@Param("companyId") Long companyId,
                                         @Param("status") String status,
                                         @Param("dept") String dept);
}
