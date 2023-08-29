package com.example.dsworker.exceptionhandler;

import com.example.dsworker.dto.response.R;
import com.example.dsworker.exception.DatabaseDriverFoundException;
import jakarta.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 处理全局的 SQLException
     * @param e
     * @return
     */
    @ResponseBody
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ExceptionHandler({ SQLException.class })
    public R<Object> handleSqlException(@NotNull Exception e) {
        return R.badRequest("SQL 异常，请重新检查相关配置。错误：" + e.getMessage());
    }

    /**
     * 处理全局的 DatabaseDriverFoundException
     * @param e
     * @return
     */
    @ResponseBody
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ExceptionHandler({ DatabaseDriverFoundException.class })
    public R<Object> handleClassNotFoundException(@NotNull Exception e) {
        return R.badRequest("数据库驱动未找到，请更换驱动或联系管理员进行系统升级");
    }
}