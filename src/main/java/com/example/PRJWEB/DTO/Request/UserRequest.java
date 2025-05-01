package com.example.PRJWEB.DTO.Request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
public class UserRequest {
    @NotBlank(message = "USERNAME_REQUIRED")
    @Size (min = 3, message = "USER_INVALID")
    String username;
    @NotBlank(message = "PASSWORD_REQUIRED")
    @Size(min = 8 , message = "PASSWORD_INVALID")
    String password;
    @Email(message = "EMAIL_INVALID")
    @NotBlank(message = "EMAIL_REQUIRED")
    String email;
    String fullname;
    String address;
    Set<String> role;
    String phone;
    @JsonFormat(pattern = "yyyy-MM-dd")
    LocalDate dateOfBirth;
    String citizenId;
    String avatar;
}
