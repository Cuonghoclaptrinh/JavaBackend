package com.example.PRJWEB.Exception;


import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)

public enum ErrorCode {

    UNCATEGORIZE_EXCEPTION(999 , "Uncategorized error" , HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_KEY(1000, "Invalid message key" , HttpStatus.BAD_REQUEST),
    USER_EXISTED( 1001 , "User existed", HttpStatus.BAD_REQUEST),
    PASSWORD_INVALID(1002, "Password must be at least 8 characters" , HttpStatus.BAD_REQUEST),
    PASSWORD_REQUIRED(1002, "Password required" ,HttpStatus.BAD_REQUEST),
    USERNAME_INVALID(1003, "Username must be at least 3 characters" ,HttpStatus.BAD_REQUEST),
    USERNAME_REQUIRED(1003, "Username required" ,HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED( 1004 , "User not existed" ,HttpStatus.NOT_FOUND),
    UNAUTHENTICATED( 1005 , "unauthenticated",HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1008, "You do not have permission", HttpStatus.FORBIDDEN),

    TOUR_NOT_EXISTED(1010,"Tout not existed" , HttpStatus.NOT_FOUND),
    BOOKING_NOT_FOUND(1011 , "booking not found" , HttpStatus.NOT_FOUND),
    PAYMENT_AMOUNT_EXCEED(1012 , "payment amount exceed" , HttpStatus.BAD_REQUEST),
    PAYMENT_ALREADY_COMPLETED(1013 , "payment already completed" , HttpStatus.BAD_REQUEST),
    INVALID_BOOKING_STATUS(1013 , "invalid booking status" , HttpStatus.BAD_REQUEST),
    INVALID_PAYMENT_AMOUNT(1013 , "invalid payment amount" , HttpStatus.BAD_REQUEST),

    ;
    int code;
    String message;
    private HttpStatusCode httpStatusCode;

    ErrorCode(int code , String message ,HttpStatusCode httpStatusCode) {
        this.code=code;
        this.message=message;
        this.httpStatusCode = httpStatusCode;
    }
}
