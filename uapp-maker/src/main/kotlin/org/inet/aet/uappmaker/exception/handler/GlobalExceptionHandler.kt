package org.inet.aet.uappmaker.exception.handler

import jakarta.validation.ConstraintViolationException
import jakarta.validation.ValidationException
import jakarta.validation.constraints.NotNull
import lombok.extern.slf4j.Slf4j
import org.inet.aet.uappmaker.dto.response.R
import org.inet.aet.uappmaker.dto.response.R_ERROR
import org.inet.aet.uappmaker.dto.response.R_SUCCESS
import org.inet.aet.uappmaker.exception.BaseBuzException
import org.springframework.http.HttpStatus
import org.springframework.validation.BindException
import org.springframework.validation.ObjectError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
@Slf4j
class GlobalExceptionHandler {
    /**
     * 拦截参数校验错误
     * @param e
     * @return
     */
    @ResponseBody
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(
        BindException::class, ValidationException::class, MethodArgumentNotValidException::class
    )
    fun handleParameterVerificationException(e: Exception): R<Any?> {
        val sb = StringBuilder()
        if (e is BindException) {
            val bindingResult = e.bindingResult
            bindingResult.allErrors.stream()
                .map { obj: ObjectError -> obj.defaultMessage }
                .forEach { str: String? -> sb.append(str) }
        } else if (e is ConstraintViolationException) {
            if (e.message != null) {
                sb.append(e.message)
            }
        } else {
            sb.append("invalid params")
        }
        val message = sb.toString()
        return R_ERROR(null, message)
    }

    @ResponseBody
    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(
        BaseBuzException::class
    )
    fun handleBusinessException(e: BaseBuzException): R<Any?> {
        return R_ERROR(null, e.message)
    }
}
