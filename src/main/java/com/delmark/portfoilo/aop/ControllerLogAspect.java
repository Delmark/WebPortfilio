package com.delmark.portfoilo.aop;


import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Aspect
@Component
@Slf4j
public class ControllerLogAspect {

    @Pointcut("execution(public * com.delmark.portfoilo.controller.*.*(..))")
    public void handleControllerPointcut() {}

    @Before("handleControllerPointcut()")
    public void beforeControllerExecution(JoinPoint joinPoint) {
        List<String> args = Arrays.stream(joinPoint.getArgs()).map(Object::toString).toList();
        log.info("Executing controller {} with args {}", joinPoint.getSignature().getName(), joinPoint.getArgs());
    }

    @AfterReturning(value = "handleControllerPointcut()", returning = "response")
    public void afterControllerExecution(JoinPoint joinPoint, ResponseEntity<?> response) {
        log.info("Controller {} returned response with status code: {}", joinPoint.getSignature(), response.getBody(), response.getStatusCode());
    }
}
