package com.example.PRJWEB.DTO.Respon;

import com.example.PRJWEB.Enums.Roles;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Set;

@AllArgsConstructor
@NoArgsConstructor
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class UserResponse {
    Integer id;
    String username;
    String email;
    Set<String> roles;
    String fullname;
    String phone;
    String address;
    LocalDate dateOfBirth;
    String citizenId;
    String avatar;
}
