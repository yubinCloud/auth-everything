package com.example.afleader.exception.handler;

import com.example.afleader.dto.response.R;
import com.example.afleader.exception.DynamicRouteOpsException;
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
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ExceptionHandler({DynamicRouteOpsException.class})
    public R<Object> handlerDynamicRouteOpsException(@NotNull Exception e) {
        return R.badRequest(e.getMessage());
    }
}
