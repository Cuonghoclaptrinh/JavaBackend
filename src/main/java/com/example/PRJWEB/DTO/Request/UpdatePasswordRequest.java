package com.example.PRJWEB.DTO.Request;

import lombok.Data;

@Data
public class UpdatePasswordRequest {
    private String email;
    private String newPassword;
}
