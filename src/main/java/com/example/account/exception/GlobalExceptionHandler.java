package com.example.account.exception;

import com.example.account.Type.ErrorCode;
import com.example.account.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.example.account.Type.ErrorCode.INTERNAL_SERVER_ERROR;
import static com.example.account.Type.ErrorCode.INVALID_REQUEST;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(AccountException.class)
    public ErrorResponse handelAccountException(AccountException e){
        log.error("{} is occured", e.getErrorCode());

        return new ErrorResponse(e.getErrorCode(), e.getErrorMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ErrorResponse handleMethodArgumentNotValidException(MethodArgumentNotValidException e){
        log.error("handleDataIntegerityViolationException is occured",e);

        return new ErrorResponse(INVALID_REQUEST,INVALID_REQUEST.getDescription());

    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ErrorResponse handleDataIntegerityViolationException(DataIntegrityViolationException e){
        log.error("handleDataIntegerityViolationException is occured",e);

        return new ErrorResponse(INVALID_REQUEST,INVALID_REQUEST.getDescription());

    }


    @ExceptionHandler(Exception.class)
    public ErrorResponse handelAccountException(Exception e){
        log.error("Exception is occured", e);

        return new ErrorResponse(
                INTERNAL_SERVER_ERROR,
                INTERNAL_SERVER_ERROR.getDescription());
    }

}
