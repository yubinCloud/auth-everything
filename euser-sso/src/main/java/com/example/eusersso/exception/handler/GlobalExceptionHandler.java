package com.example.eusersso.exception.handler;

import com.example.eusersso.exception.BaseBusinessException;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.ValidationException;
import jakarta.validation.constraints.NotNull;
import com.example.eusersso.dto.response.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 拦截参数校验错误
     * @param e
     * @return
     */
    @ResponseBody
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ExceptionHandler({ BindException.class, ValidationException.class, MethodArgumentNotValidException.class })
    public R<Object> handleParameterVerificationException(@NotNull Exception e) {
        StringBuilder sb = new StringBuilder();
        if (e instanceof BindException) {
            BindingResult bindingResult = ((BindException) e).getBindingResult();
            bindingResult.getAllErrors().stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .forEach(sb::append);
        } else if (e instanceof ConstraintViolationException) {
            if (e.getMessage() != null) {
                sb.append(e.getMessage());
            }
        } else {
            sb.append("invalid params");
        }
        String message = sb.toString();
        return R.badRequest(message);
    }

    @ResponseBody
    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(BaseBusinessException.class)
    public R<Object> handleBusinessException(BaseBusinessException businessException) {
        log.error(businessException.getLocalizedMessage(), businessException);
        return R.error(businessException.getMessage());
    }

}
