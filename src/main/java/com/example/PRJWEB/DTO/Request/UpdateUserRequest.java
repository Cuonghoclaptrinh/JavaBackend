package com.example.PRJWEB.DTO.Request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty("email")
    String email;
    @JsonProperty("fullname")
    String fullname;
    @JsonProperty("address")
    String address;
    @JsonProperty("role")
    Set<String> role;
    @JsonProperty("phone")
    String phone;
    @JsonFormat(pattern = "yyyy-MM-dd")
    @JsonProperty("dateOfBirth")
    LocalDate dateOfBirth;
    @JsonProperty("citizenId")
    String citizenId;
    @JsonProperty("avatar")
    String avatar;
}
