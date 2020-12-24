package com.pebblepost.cpp_sync.aop.logging;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

@Aspect
public class LoggingAspect {

  @Pointcut(
      "within(@org.springframework.stereotype.Component *)"
          + "|| within(@org.springframework.stereotype.Repository *)"
          + "|| within(@org.springframework"
          + ".stereotype.Service *)")
  public void springBeanPointcut() {}

  @AfterThrowing(pointcut = "springBeanPointcut()", throwing = "e")
  public void logAfterThrowing(JoinPoint joinPoint, Throwable e) {
    logger(joinPoint)
        .error(
            "Exception in {}::{}() with cause = '{}' and exception = '{}'",
            joinPoint.getSignature().getDeclaringTypeName(),
            joinPoint.getSignature().getName(),
            e.getCause() != null ? e.getCause() : "NULL",
            e.getMessage(),
            e);
  }

  private Logger logger(JoinPoint joinPoint) {
    return LoggerFactory.getLogger(joinPoint.getSignature().getDeclaringTypeName());
  }

  @Around("springBeanPointcut()")
  public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
    Logger log = logger(joinPoint);
    log.debug(
        "Enter: {}::{}() with argument[s] = {}",
        joinPoint.getSignature().getDeclaringTypeName(),
        joinPoint.getSignature().getName(),
        Arrays.toString(joinPoint.getArgs()));
    try {
      Object result = joinPoint.proceed();
      log.debug(
          "Exit: {}::{}() with result = {}",
          joinPoint.getSignature().getDeclaringTypeName(),
          joinPoint.getSignature().getName(),
          result);
      return result;
    } catch (IllegalArgumentException e) {
      log.error(
          "Illegal argument: {} in {}::{}()",
          Arrays.toString(joinPoint.getArgs()),
          joinPoint.getSignature().getDeclaringTypeName(),
          joinPoint.getSignature().getName());
      throw e;
    }
  }
}
