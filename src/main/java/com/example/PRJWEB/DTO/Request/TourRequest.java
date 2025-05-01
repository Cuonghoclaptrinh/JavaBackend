package com.example.PRJWEB.DTO.Request;

import com.example.PRJWEB.Enums.TourType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class TourRequest {
    private String tourName; // ten_tour
    private BigDecimal price; // gia
    private String duration; // thoi_luong
    private String description; // mo_ta
    private String itinerary; // lich_trinh
    private String transportation; // phuong_tien
    private String accommodation; // noi_o
    private TourType tourType; // loai_tour
    private String region; // khu_vuc
    private BigDecimal discount; // uu_dai
    private BigDecimal newPrice; // gia_moi
    private List<String> images = new ArrayList<>();

    private int peopleLimit; // gioi_han_nguoi
    private LocalDate departureDate;
}
