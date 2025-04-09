package com.example.PRJWEB.DTO.Request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDate;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateUserRequest {
    String email;
    String fullname;
    String address;
    Set<String> role;
    String phone;
    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate dateOfBirth;
    String citizenId;
}
