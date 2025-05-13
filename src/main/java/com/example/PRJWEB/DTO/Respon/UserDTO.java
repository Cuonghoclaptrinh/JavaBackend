package com.example.PRJWEB.DTO.Respon;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long id; // Phù hợp với User.id
    private String name;
    private String phone;
    private String email;
}