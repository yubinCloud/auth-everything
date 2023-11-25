package com.example.eusersso.aspect;

import com.example.eusersso.config.ShanDongTongProperties;
import com.example.eusersso.exception.ShanDongTongNotEnabledException;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 利用 AOP 拦截当“山东通”未开启时的 Controller 方法
 */
@Aspect
@Component
@RequiredArgsConstructor
public class ShanDongTongSSOAdapterControllerAspect {

    private final ShanDongTongProperties shanDongTongProperties;

    @Pointcut("execution(public * com.example.eusersso.controller.ShanDongTongSSOAdapterController.*(..))")
    public void pointCut() {}

    @Before("pointCut()")
    public void beforeController(JoinPoint joinPoint) {
        if (Objects.isNull(shanDongTongProperties.getEnabled()) || !shanDongTongProperties.getEnabled()) {
            throw new ShanDongTongNotEnabledException();
        }
    }
}
