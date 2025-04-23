package com.example.PRJWEB.Controller;

import com.example.PRJWEB.DTO.Request.ApiResponse;
import com.example.PRJWEB.DTO.Request.TourRequest;
import com.example.PRJWEB.DTO.Request.TourScheduleRequest;
import com.example.PRJWEB.DTO.Respon.TourResponse;
import com.example.PRJWEB.Enums.TourType;
import com.example.PRJWEB.Service.TourService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/tours")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
public class TourController {
    TourService tourService;

    @PostMapping
    public ApiResponse<TourResponse> addTour(@RequestBody @Valid TourRequest request)  {
        System.out.println("Price from request: " + request.getPrice());
        return ApiResponse.<TourResponse>builder()
                .message("Thêm tour thành công")
                .result(tourService.addTour(request))
                .build();
    }

    @PutMapping(value ="/{id}" )
    public ApiResponse<TourResponse> updateTour(@PathVariable("id") Integer id,@RequestBody @Valid TourRequest request)  {
        return ApiResponse.<TourResponse>builder()
                .message("Cập nhật tour thành công")
                .result(tourService.updateTour(id,request))
                .build();
    }

    @GetMapping
    public ApiResponse<List<TourResponse>> getAllTours() {
        List<TourResponse> list = tourService.getTour();
        return ApiResponse.<List<TourResponse>>builder()
                .message("Lấy danh sách tour thành công")
                .result(list)
                .build();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteTour(@PathVariable("id") Integer id) {
        tourService.deleteTour(id);
        return ApiResponse.<Void>builder()
                .message("Xoá tour thành công")
                .build();
    }

    @PostMapping("/{tourId}/schedule")
    public ApiResponse<String> addSchedule(
            @PathVariable Integer tourId,
            @RequestBody TourScheduleRequest scheduleRequest
    ) {
        tourService.addScheduleToTour(tourId, scheduleRequest);
        return ApiResponse.<String>builder()
                .message("Thêm lịch khởi hành mới thành công!")
                .result("OK")
                .build();
    }


    @GetMapping("/filter")
    public ApiResponse<List<TourResponse>> filterTour(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String region,
            @RequestParam(required = false) String tourType,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice
    ) {
        List<TourResponse> filtered = tourService.filterTour(keyword, region, tourType, minPrice, maxPrice);
        return ApiResponse.<List<TourResponse>>builder()
                .message("Lọc tour thành công")
                .result(filtered)
                .build();
    }
}
