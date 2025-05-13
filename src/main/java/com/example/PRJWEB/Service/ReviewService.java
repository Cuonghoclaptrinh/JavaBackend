package com.example.PRJWEB.Service;

import com.example.PRJWEB.DTO.Request.ReviewRequest;
import com.example.PRJWEB.DTO.Respon.ReviewResponse;
import com.example.PRJWEB.Entity.Review;
import com.example.PRJWEB.Exception.AppException;
import com.example.PRJWEB.Exception.ErrorCode;
import com.example.PRJWEB.Repository.ReviewRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReviewService {
    ReviewRepository reviewRepository;

    public List<ReviewResponse> getReviewsByTourId(String tourId, int limit) {
        if (tourId == null || tourId.isEmpty()) {
            throw new AppException(ErrorCode.INVALID_TOUR_ID);
        }
        PageRequest pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "createdAt"));
        List<Review> reviews = reviewRepository.findByTourIdOrderByCreatedAtDesc(tourId, pageable);
        return reviews.stream()
                .map(review -> ReviewResponse.builder()
                        .id(review.getId())
                        .tourId(review.getTourId())
                        .name(review.getName())
                        .rating(review.getRating())
                        .comment(review.getComment())
                        .createdAt(review.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public List<ReviewResponse> getAllReviews(int page, int limit) {
        PageRequest pageable = PageRequest.of(page, limit, Sort.by(Sort.Direction.DESC, "createdAt"));
        List<Review> reviews = reviewRepository.findAll(pageable).getContent();
        return reviews.stream()
                .map(review -> ReviewResponse.builder()
                        .id(review.getId())
                        .tourId(review.getTourId())
                        .name(review.getName())
                        .rating(review.getRating())
                        .comment(review.getComment())
                        .createdAt(review.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }


    @PreAuthorize("hasAnyAuthority('ADMIN','STAFF','USER')")
    public ReviewResponse createReview(String tourId, ReviewRequest request) {
        if (tourId == null || tourId.isEmpty()) {
            throw new AppException(ErrorCode.INVALID_TOUR_ID);
        }
        if (request.rating() < 1 || request.rating() > 5) {
            throw new AppException(ErrorCode.INVALID_RATING);
        }
        if (request.comment() == null || request.comment().trim().isEmpty()) {
            throw new AppException(ErrorCode.INVALID_COMMENT);
        }

        Review review = Review.builder()
                .tourId(tourId)
                .name(request.name())
                .rating(request.rating())
                .comment(request.comment().trim())
                .createdAt(LocalDateTime.now())
                .build();

        Review savedReview = reviewRepository.save(review);

        return ReviewResponse.builder()
                .id(savedReview.getId())
                .tourId(savedReview.getTourId())
                .name(savedReview.getName())
                .rating(savedReview.getRating())
                .comment(savedReview.getComment())
                .createdAt(savedReview.getCreatedAt())
                .build();
    }
}