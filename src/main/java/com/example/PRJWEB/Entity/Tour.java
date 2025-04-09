package com.example.PRJWEB.Entity;

import com.example.PRJWEB.Enums.TourType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.math.BigDecimal;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class Tour {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer tourId; // ma_tour -> tourId

    @Column(name = "tour_name", nullable = false)
    String tourName; // ten_tour -> tourName

    @Column(name = "price", nullable = false)
    BigDecimal price; // gia -> price

    @Column(name = "duration")
    String duration; // thoi_luong -> duration

    @Column(name = "description")
    String description; // mo_ta -> description

    @Column(name = "itinerary")
    String itinerary; // lich_trinh -> itinerary

    @Column(name = "transportation")
    String transportation; // phuong_tien -> transportation

    @Column(name = "people_limit")
    Integer peopleLimit; // gioi_han_nguoi -> peopleLimit

    @Column(name = "accommodation")
    String accommodation; // noi_o -> accommodation

    @Column(name = "tour_type", nullable = false)
    @Enumerated(EnumType.STRING)
    TourType tourType; // loai_tour -> tourType

    @Column(name = "region")
    String region; // khu_vuc -> region

    @Column(name = "discount")
    BigDecimal discount; // uu_dai -> discount

    @Column(name = "new_price")
    BigDecimal newPrice; // gia_moi -> newPrice

    @Column(name = "image")
    String image; // hinh_anh -> image
}
