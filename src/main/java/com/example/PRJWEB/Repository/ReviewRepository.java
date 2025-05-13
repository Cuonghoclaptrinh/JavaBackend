package com.example.PRJWEB.Repository;

import com.example.PRJWEB.Entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByTourIdOrderByCreatedAtDesc(String tourId, org.springframework.data.domain.Pageable pageable);
}