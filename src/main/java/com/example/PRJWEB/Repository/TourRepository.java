package com.example.PRJWEB.Repository;

import com.example.PRJWEB.Entity.Tour;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TourRepository extends JpaRepository<Tour, Integer> {

    // Tìm tất cả tour theo loại (nội địa hoặc quốc tế)
    List<Tour> findByTourType(String tourType);

    // Tìm tour theo khu vực (dành cho tour nội địa)
    List<Tour> findByRegion(String region);

    // Tìm tour theo tên
    List<Tour> findByTourNameContaining(String tourName);
}
