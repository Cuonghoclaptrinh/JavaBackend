package com.example.PRJWEB.Repository;

import com.example.PRJWEB.Entity.Tour;
import com.example.PRJWEB.Enums.TourType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface TourRepository extends JpaRepository<Tour, Integer> {

    // Tìm tất cả tour theo loại (nội địa hoặc quốc tế)
    List<Tour> findByTourType(String tourType);

    // Tìm tour theo khu vực (dành cho tour nội địa)
    List<Tour> findByRegion(String region);

    // Tìm tour theo tên
    List<Tour> findByTourNameContaining(String tourName);

    @Query("SELECT t FROM Tour t WHERE " +
            "(:keyword IS NULL OR LOWER(t.tourName) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
            "(:region IS NULL OR t.region = :region) AND " +
            "(:tourType IS NULL OR t.tourType = :tourType) AND " +
            "(:minPrice IS NULL OR t.price >= :minPrice) AND " +
            "(:maxPrice IS NULL OR t.price <= :maxPrice)")
    List<Tour> filterTours(@Param("keyword") String keyword,
                           @Param("region") String region,
                           @Param("tourType") TourType tourType,
                           @Param("minPrice") BigDecimal minPrice,
                           @Param("maxPrice") BigDecimal maxPrice);

}
