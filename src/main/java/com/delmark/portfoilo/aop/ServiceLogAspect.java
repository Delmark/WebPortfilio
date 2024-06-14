package com.delmark.portfoilo.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Aspect
@Component
@Slf4j
public class ServiceLogAspect {

    @Pointcut("execution(public * com.delmark.portfoilo.service..*(..))")
    public void servicePointcut() {}

    @Before("servicePointcut()")
    public void beforeServiceExecution(JoinPoint joinPoint) {
        List<String> args = Arrays.stream(joinPoint.getArgs()).map(Object::toString).toList();
        log.info("Executing service {} with args {}", joinPoint.getSignature().getName(), args);
    }

    @AfterReturning(value = "servicePointcut()", returning = "object")
    public void afterServiceExecution(JoinPoint joinPoint, Object object) {
        log.info("Service {} returned {}", joinPoint.getSignature().getName(), (object == null) ? "null" : object.toString());
    }
}
