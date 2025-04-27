package com.example.PRJWEB.Service;

import com.example.PRJWEB.Entity.Favorite;
import com.example.PRJWEB.Entity.Tour;
import com.example.PRJWEB.Entity.User;
import com.example.PRJWEB.Repository.FavoriteRepository;
import com.example.PRJWEB.Repository.TourRepository;
import com.example.PRJWEB.Repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FavoriteService {
    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TourRepository tourRepository;

    public List<Integer> getFavorites() {
        User user = getCurrentUser();
        List<Favorite> favorites = favoriteRepository.findByUser(user);
        return favorites.stream()
                .map(favorite -> favorite.getTour().getTourId())
                .collect(Collectors.toList());
    }

    // Thêm tour vào yêu thích
    @Transactional
    public void addFavorite(Integer tourId) {
        User user = getCurrentUser();
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new RuntimeException("Tour không tồn tại"));
        if (!favoriteRepository.findByUserAndTour(user, tour).isPresent()) {
            Favorite favorite = Favorite.builder()
                    .user(user)
                    .tour(tour)
                    .build();
            favoriteRepository.save(favorite);
        }
    }

    // Xóa tour khỏi yêu thích
    @Transactional
    public void removeFavorite(Integer tourId) {
        User user = getCurrentUser();
        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new RuntimeException("Tour không tồn tại"));
        favoriteRepository.deleteByUserAndTour(user, tour);
    }

    // Lấy người dùng hiện tại từ SecurityContext
    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));
    }
}