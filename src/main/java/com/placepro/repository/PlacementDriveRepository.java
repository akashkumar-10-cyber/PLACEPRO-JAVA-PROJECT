package com.placepro.repository;

import com.placepro.model.PlacementDrive;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlacementDriveRepository extends JpaRepository<PlacementDrive, Long> {
    List<PlacementDrive> findByStatus(String status);
    long countByStatus(String status);
}
