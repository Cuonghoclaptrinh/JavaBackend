package com.example.PRJWEB.Controller;

import com.example.PRJWEB.DTO.Request.ApiResponse;
import com.example.PRJWEB.DTO.Request.AuthenticationRequest;
import com.example.PRJWEB.DTO.Request.IntrospectRequest;
import com.example.PRJWEB.DTO.Respon.AuthenticationResponse;
import com.example.PRJWEB.DTO.Respon.IntrospectResponse;
import com.example.PRJWEB.Service.AuthenticationService;
import com.nimbusds.jose.JOSEException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {
    AuthenticationService authenticationService;

    @PostMapping("/token")
    ApiResponse<AuthenticationResponse> authenticate (@RequestBody AuthenticationRequest request){
        var result  = authenticationService.authenticate(request);
        return ApiResponse.<AuthenticationResponse>builder()
                .result(result)
                .build();
    }

    @PostMapping("/introspect")
    ApiResponse<IntrospectResponse> authenticate(@RequestBody IntrospectRequest request)
        throws ParseException , JOSEException {
        var result = authenticationService.introspect(request);
        return ApiResponse.<IntrospectResponse>builder()
                .result(result)
                .build();

    }
}
