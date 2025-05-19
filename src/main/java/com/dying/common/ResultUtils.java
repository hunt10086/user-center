package com.dying.common;


public class ResultUtils {

    public static<T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(0,data,"ok");
    }

    public static<T> BaseResponse<T>error(ErrorCode errorCode) {
        return new BaseResponse<>(errorCode);
    }
    public static BaseResponse error(int code,String message,String description) {
        return new BaseResponse(code,message,description);
    }

    public static BaseResponse error(ErrorCode errorCode,String message,String description) {
        return new BaseResponse(errorCode.getCode(),message,description);
    }

/*    public static BaseResponse error(ErrorCode errorCode,String description) {
        return new BaseResponse(errorCode.getCode(),errorCode.getMessage(),description);
    }*/
}
