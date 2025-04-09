package com.example.PRJWEB.Exception;

import com.example.PRJWEB.DTO.Request.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = Exception.class)
    ResponseEntity<ApiResponse> handlingRuntimeException(Exception exception){
        ApiResponse apiResponse = ApiResponse.builder()
                .code(ErrorCode.UNCATEGORIZE_EXCEPTION.getCode())
                .message(ErrorCode.UNCATEGORIZE_EXCEPTION.getMessage())
                .build();
        return ResponseEntity.badRequest().body(apiResponse);
    }

    @ExceptionHandler(value = AppException.class)
    ResponseEntity<ApiResponse> handlingAppException(AppException exception){

        ApiResponse apiResponse = ApiResponse.builder()
                .code(exception.getErrorCode().getCode())
                .message(exception.getErrorCode().getMessage())
                .build();
        return ResponseEntity.status(exception.getErrorCode().getHttpStatusCode()).body(apiResponse);
    }

    @ExceptionHandler(value = AccessDeniedException.class)
    ResponseEntity<ApiResponse> handlingAccessDenied(AccessDeniedException exception){
        ErrorCode errorCode  = ErrorCode.UNAUTHORIZED;
        return ResponseEntity.status(errorCode.getHttpStatusCode()).body(
                ApiResponse.builder()
                        .code(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build()
        );
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    ResponseEntity<ApiResponse> handlingValidation(MethodArgumentNotValidException exception){
        String enumkey = exception.getFieldError().getDefaultMessage();

        ErrorCode errorCode = ErrorCode.INVALID_KEY;
        try{
            errorCode = ErrorCode.valueOf(enumkey);
        }catch (IllegalArgumentException e){
            errorCode = ErrorCode.INVALID_KEY;
        }

        ApiResponse apiResponse = ApiResponse.builder()
                .code(errorCode.getCode())
                .message((errorCode.getMessage()))
                .build();

        return ResponseEntity.badRequest().body(apiResponse);
    }
}
