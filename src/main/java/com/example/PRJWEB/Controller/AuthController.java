package com.example.PRJWEB.Controller;

import com.example.PRJWEB.DTO.Respon.AuthResponse;
import com.example.PRJWEB.Entity.User;
import com.example.PRJWEB.Enums.Roles;
import com.example.PRJWEB.Exception.AppException;
import com.example.PRJWEB.Exception.ErrorCode;
import com.example.PRJWEB.Service.AuthenticationService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import com.example.PRJWEB.DTO.Request.GoogleLoginRequest;
import com.example.PRJWEB.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostMapping("/google")
    public ResponseEntity<AuthResponse> loginWithGoogle(@RequestBody GoogleLoginRequest request) {
        try {
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(request.getIdToken());
            String uid = decodedToken.getUid();
            String email = decodedToken.getEmail();
            String name = decodedToken.getName() != null ? decodedToken.getName() : email.split("@")[0];

            User user = userService.findByUid(uid);
            if (user == null) {
                User existingUser = userService.findByEmail(email);
                if (existingUser != null) {
                    if (existingUser.getUid() == null) {
                        existingUser.setUid(uid);
                        userService.updateUser(existingUser.getId(), existingUser);
                        user = existingUser;
                    } else {
                        throw new AppException(ErrorCode.EMAIL_EXISTED);
                    }
                } else {
                    user = new User();
                    user.setUid(uid);
                    user.setEmail(email);
                    String baseUsername = name.replaceAll("[^a-zA-Z0-9]", "").toLowerCase();
                    user.setUsername(userService.generateUniqueUsername(baseUsername));
                    user.setRoles(Set.of(Roles.USER.name()));
                    userService.createUser(user);
                }
            }

            String token = authenticationService.createTokenForUser(user);
            return ResponseEntity.ok(new AuthResponse(token, true));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(new AuthResponse(null, false));
        }
    }

    @PostMapping("/update-password")
//    @PreAuthorize("#id == authentication.principal.claims['user_id']")
    public ResponseEntity<Map<String, String>> updatePassword(
            @RequestParam Long id,
            @RequestParam String newPassword
    ) {
        try {
            User user = userService.findById(id.intValue());
            if (user == null) {
                throw new AppException(ErrorCode.USER_NOT_EXISTED);
            }
            user.setPassword(passwordEncoder.encode(newPassword));
            userService.updateUser(id, user);
            return ResponseEntity.ok(Map.of("message", "Password updated successfully"));
        } catch (Exception e) {
            System.err.println("Update password error: " + e.getMessage());
            return ResponseEntity.status(400).body(Map.of("error", e.getMessage()));
        }
    }
}