package com.example.afencryptiongateway.exceptionhandler;

import com.example.afencryptiongateway.dto.response.R;
import com.example.afencryptiongateway.exception.ForbidRequestException;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ResponseBody
    @ResponseStatus(code = HttpStatus.FORBIDDEN)
    @ExceptionHandler({ForbidRequestException.class})
    public R<Object> handleForbidRequestException(@NotNull ForbidRequestException e) {
        return R.forbidden(e.getMessage());
    }
}
