package com.example.PRJWEB.Exception;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE , makeFinal = true)
public class AppException extends RuntimeException{
    ErrorCode errorCode;

    public AppException(ErrorCode errorCode){
        super(errorCode.getMessage());
        this.errorCode=errorCode;
    }

    public ErrorCode getErrorCode(){
        return errorCode;
    }

}
