package com.example.PRJWEB.DTO.Request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AssignEmployeeRequest {
    Integer employeeId; // ID của nhân viên được phân công
}