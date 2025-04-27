package com.example.PRJWEB.Repository;

import com.example.PRJWEB.Entity.Favorite;
import com.example.PRJWEB.Entity.Tour;
import com.example.PRJWEB.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    Optional<Favorite> findByUserAndTour(User user, Tour tour);
    void deleteByUserAndTour(User user, Tour tour);
    List<Favorite> findByUser(User user);
}