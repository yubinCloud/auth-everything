package com.example.jupytergateway.exceptionhandler;

import com.example.jupytergateway.dto.response.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ResponseBody
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public R<Object> handleOtherException(Exception e) {
        log.error(e.toString(), e);
        return R.error(e.getLocalizedMessage());
    }
}
