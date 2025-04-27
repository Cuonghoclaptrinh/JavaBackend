package com.example.PRJWEB.Controller;

import com.example.PRJWEB.DTO.Request.FavoriteRequest;
import com.example.PRJWEB.Service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {
    @Autowired
    private FavoriteService favoriteService;

    @PreAuthorize("hasAnyAuthority('USER')")
    @GetMapping
    public ResponseEntity<List<Integer>> getFavorites() {
        List<Integer> favorites = favoriteService.getFavorites();
        return ResponseEntity.ok(favorites);
    }
    // Thêm tour vào yêu thích
    @PreAuthorize("hasAnyAuthority('USER')")
    @PostMapping
    public ResponseEntity<Void> addFavorite(@RequestBody FavoriteRequest request) {
        favoriteService.addFavorite(request.getTourId());
        return ResponseEntity.ok().build();
    }

    // Xóa tour khỏi yêu thích
    @PreAuthorize("hasAnyAuthority('USER')")
    @DeleteMapping("/{tourId}")
    public ResponseEntity<Void> removeFavorite(@PathVariable Integer tourId) {
        favoriteService.removeFavorite(tourId);
        return ResponseEntity.ok().build();
    }
}