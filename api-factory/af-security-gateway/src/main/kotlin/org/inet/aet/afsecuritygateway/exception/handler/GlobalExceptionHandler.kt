package org.inet.aet.afsecuritygateway.exception.handler

import org.inet.aet.afsecuritygateway.dto.response.R
import org.inet.aet.afsecuritygateway.dto.response.R_FORBIIDEN
import org.inet.aet.afsecuritygateway.exception.AbsentPermissionException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalExceptionHandler {

    @ResponseBody
    @ResponseStatus(code = HttpStatus.FORBIDDEN)
    @ExceptionHandler(AbsentPermissionException::class)
    fun handleAbsentPermissionException(e: AbsentPermissionException): R<Any> {
        return R_FORBIIDEN("权限不足，禁止访问")
    }
}