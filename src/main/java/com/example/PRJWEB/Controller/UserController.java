    package com.example.PRJWEB.Controller;

    import com.example.PRJWEB.DTO.Request.ApiResponse;
    import com.example.PRJWEB.DTO.Request.UpdateUserRequest;
    import com.example.PRJWEB.DTO.Request.UserRequest;
    import com.example.PRJWEB.DTO.Respon.UserResponse;
    import com.example.PRJWEB.Service.UserService;
    import jakarta.validation.Valid;
    import lombok.AccessLevel;
    import lombok.RequiredArgsConstructor;
    import lombok.experimental.FieldDefaults;
    import org.springframework.security.access.prepost.PreAuthorize;
    import org.springframework.web.bind.annotation.*;

    import java.util.List;

    @RestController
    @RequestMapping("/users")
    @RequiredArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE,makeFinal = true)
    public class UserController {

        UserService userService;

        // Đăng ký khách hàng (USER) - Công khai
        @PostMapping("/customers")
        ApiResponse<UserResponse> register(@RequestBody @Valid UserRequest request) {
            return ApiResponse.<UserResponse>builder()
                    .result(userService.register(request))
                    .build();
        }

        // Tạo nhân viên (STAFF) - Chỉ admin
        @PostMapping("/employees")
            ApiResponse<UserResponse> createEmployee(@RequestBody @Valid UserRequest request) {
            return ApiResponse.<UserResponse>builder()
                    .result(userService.createEmployee(request))
                    .build();
        }

        // Lấy tất cả người dùng - Chỉ admin
        @GetMapping
        @PreAuthorize("hasAuthority('ADMIN')")
        ApiResponse<List<UserResponse>> getAllUsers() {
            return ApiResponse.<List<UserResponse>>builder()
                    .result(userService.getUser())
                    .build();
        }

        // Lấy người dùng theo role - Admin hoặc Staff (chỉ USER)
        @GetMapping(params = "role")
        ApiResponse<List<UserResponse>> getUsersByRole(@RequestParam String role) {
            return ApiResponse.<List<UserResponse>>builder()
                    .result(userService.getUsersByRole(role))
                    .build();
        }

        // Lấy thông tin người dùng theo ID - Chính người dùng hoặc admin
        @GetMapping("/{id}")
        ApiResponse<UserResponse> getUserById(@PathVariable int id) {
            return ApiResponse.<UserResponse>builder()
                    .result(userService.getUserById(id))
                    .build();
        }

        // Cập nhật thông tin cơ bản - Chỉ admin
        @PutMapping("/{id}")
        ApiResponse<UserResponse> updateUser(@PathVariable int id, @RequestBody @Valid UpdateUserRequest request) {
            return ApiResponse.<UserResponse>builder()
                    .result(userService.updateUser(id, request))
                    .build();
        }


        // Xóa người dùng - Chỉ admin
        @DeleteMapping("/{id}")
        ApiResponse<String> deleteUser(@PathVariable int id) {
            userService.deleteUser(id);
            return ApiResponse.<String>builder()
                    .result("Xóa thành công")
                    .build();
        }

        // Lấy thông tin của chính mình - Công khai với người dùng đã đăng nhập
        @GetMapping("/myInfo")
        ApiResponse<UserResponse> getMyInfo() {
            return ApiResponse.<UserResponse>builder()
                    .result(userService.getMyInfo())
                    .build();
        }
    }
