package com.example.PRJWEB.DTO.Respon;

import com.example.PRJWEB.Entity.TourSchedule;
import com.example.PRJWEB.Enums.TourType;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter

public class TourResponse {
    private Integer tourId; // ma_tour
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
    private List<TourScheduleResponse> tourSchedules;
}
