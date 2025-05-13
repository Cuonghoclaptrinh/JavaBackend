package com.example.PRJWEB.Controller;


import com.example.PRJWEB.DTO.Request.ApiResponse;
import com.example.PRJWEB.DTO.Request.ReviewRequest;
import com.example.PRJWEB.DTO.Respon.ReviewResponse;
import com.example.PRJWEB.Service.ReviewService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReviewController {
    ReviewService reviewService;

    @GetMapping("/{tourId}")
    public ApiResponse<List<ReviewResponse>> getReviews(
            @PathVariable String tourId,
            @RequestParam(defaultValue = "10") int limit) {
        List<ReviewResponse> reviews = reviewService.getReviewsByTourId(tourId, limit);
        return ApiResponse.<List<ReviewResponse>>builder()
                .message("Reviews retrieved successfully")
                .result(reviews)
                .build();
    }

    @PostMapping("/{tourId}")
    public ApiResponse<ReviewResponse> createReview(
            @PathVariable String tourId,
            @RequestBody ReviewRequest request) {
        ReviewResponse review = reviewService.createReview(tourId, request);
        return ApiResponse.<ReviewResponse>builder()
                .message("Review created successfully")
                .result(review)
                .build();
    }

    @GetMapping("/all")
    public ApiResponse<List<ReviewResponse>> getAllReviews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int limit) {
        List<ReviewResponse> reviews = reviewService.getAllReviews(page, limit);
        return ApiResponse.<List<ReviewResponse>>builder()
                .message("All reviews retrieved successfully")
                .result(reviews)
                .build();
    }
}