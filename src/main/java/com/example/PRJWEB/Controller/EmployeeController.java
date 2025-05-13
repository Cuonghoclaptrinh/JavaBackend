package com.example.PRJWEB.Controller;


import com.example.PRJWEB.DTO.EmployeePerformanceDTO;
import com.example.PRJWEB.DTO.Respon.UserResponse;
import com.example.PRJWEB.DTO.TourBookingDTO;
import com.example.PRJWEB.DTO.TourDTO;
import com.example.PRJWEB.Service.EmployeeService;
import com.example.PRJWEB.DTO.Request.ApiResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/employee")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmployeeController {

    EmployeeService employeeService;

    @GetMapping("/orders")
    @PreAuthorize("hasAuthority('STAFF')")
    public ResponseEntity<ApiResponse<List<TourBookingDTO>>> getEmployeeOrders() {
        List<TourBookingDTO> orders = employeeService.getEmployeeOrders();
        return ResponseEntity.ok(ApiResponse.<List<TourBookingDTO>>builder()
                .message("Lấy danh sách đơn hàng thành công!")
                .result(orders)
                .build());
    }

    @PatchMapping("/orders/{orderId}/status")
    @PreAuthorize("hasAuthority('STAFF')")
    public ResponseEntity<ApiResponse<String>> updateOrderStatus(@PathVariable Long orderId, @RequestBody String status) {
        employeeService.updateOrderStatus(orderId, status);
        return ResponseEntity.ok(ApiResponse.<String>builder()
                .message("Cập nhật trạng thái đơn hàng thành công!")
                .result(null)
                .build());
    }

    @GetMapping("/performance")
    @PreAuthorize("hasAuthority('STAFF')")
    public ResponseEntity<ApiResponse<EmployeePerformanceDTO>> getEmployeePerformance() {
        EmployeePerformanceDTO performance = employeeService.getEmployeePerformance();
        return ResponseEntity.ok(ApiResponse.<EmployeePerformanceDTO>builder()
                .message("Lấy số liệu hiệu suất thành công!")
                .result(performance)
                .build());
    }

    @GetMapping("/tours")
    @PreAuthorize("hasAuthority('STAFF')")
    public ResponseEntity<ApiResponse<List<TourDTO>>> getEmployeeTours() {
        List<TourDTO> tours = employeeService.getEmployeeTours();
        return ResponseEntity.ok(ApiResponse.<List<TourDTO>>builder()
                .message("Lấy danh sách tour thành công!")
                .result(tours)
                .build());
    }

    @GetMapping("/profile")
    @PreAuthorize("hasAuthority('STAFF')")
    public ResponseEntity<ApiResponse<UserResponse>> getEmployeeProfile() {
        UserResponse profile = employeeService.getEmployeeProfile();
        return ResponseEntity.ok(ApiResponse.<UserResponse>builder()
                .message("Lấy thông tin cá nhân thành công!")
                .result(profile)
                .build());
    }
}