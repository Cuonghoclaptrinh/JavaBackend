package com.example.PRJWEB.Service;

import com.example.PRJWEB.DTO.Request.UpdateUserRequest;
import com.example.PRJWEB.DTO.Request.UserRequest;
import com.example.PRJWEB.DTO.Respon.UserResponse;
import com.example.PRJWEB.Entity.User;
import com.example.PRJWEB.Enums.Roles;
import com.example.PRJWEB.Exception.AppException;
import com.example.PRJWEB.Exception.ErrorCode;
import com.example.PRJWEB.Mapper.UserMapper;
import com.example.PRJWEB.Repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE , makeFinal = true)

public class UserService {
    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    UserMapper userMapper;

    public UserResponse register(UserRequest userRequest){
        if(userRepository.existsByUsername(userRequest.getUsername()))
            throw new AppException(ErrorCode.USER_EXISTED);

        User user = userMapper.toUserRequest(userRequest);
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        Set<String> roles = new HashSet<>();
        roles.add(Roles.USER.name());
        user.setRoles(roles);
        userRepository.save(user);

        return userMapper.toUserResponse(user) ;
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    public UserResponse createEmployee(UserRequest userRequest) {
        if (userRepository.existsByUsername(userRequest.getUsername()))
            throw new AppException(ErrorCode.USER_EXISTED);

        User user = userMapper.toUserRequest(userRequest);
        user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        Set<String> roles = new HashSet<>();
        roles.add(Roles.STAFF.name());
        user.setRoles(roles);
        user = userRepository.save(user);

        return userMapper.toUserResponse(user);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    public List<UserResponse> getUser(){
        return userRepository.findAll()
                .stream().map(userMapper::toUserResponse)
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasAnyAuthority('ADMIN', 'STAFF')")
    public List<UserResponse> getUsersByRole(String role) {
        return userRepository.findByRolesContaining(role)
                .stream().map(userMapper::toUserResponse)
                .collect(Collectors.toList());
    }

    @PostAuthorize("returnObject.username == authentication.name or hasAuthority('ADMIN')")
    public UserResponse getUserById(int id){
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        return userMapper.toUserResponse(user);
    }

    @PreAuthorize("#id == authentication.principal.claims['user_id'] or hasAuthority('ADMIN')")
    public UserResponse updateUser(int id , UpdateUserRequest request){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Principal: " + auth.getPrincipal());
        System.out.println("Authorities: " + auth.getAuthorities());
        User user = userRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());
        user.setCitizenId(request.getCitizenId());
        user.setDateOfBirth(request.getDateOfBirth());
        user.setFullname(request.getFullname());
        System.out.println("DEBUG - Request Fullname: " + request.getFullname() + user.getFullname());
        //Lấy quyền hiện tại của người dùng đăng nhập
        boolean isAdmin = SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ADMIN"));

        // Chỉ admin mới được thay đổi quyền
        if (isAdmin) {
            Set<String> roles = request.getRole() != null ? new HashSet<>(request.getRole()) : new HashSet<>();
            user.setRoles(roles);
        }
        return userMapper.toUserResponse(userRepository.save(user));
    }


    @PreAuthorize("hasAuthority( 'ADMIN')")
    public void deleteUser(int id){
        userRepository.deleteById(id);
    }

    public UserResponse getMyInfo(){
        var context = SecurityContextHolder.getContext();
        String name=context.getAuthentication().getName();
        User user = userRepository.findByUsername(name).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_EXISTED));

        return userMapper.toUserResponse(user);
    }



}
