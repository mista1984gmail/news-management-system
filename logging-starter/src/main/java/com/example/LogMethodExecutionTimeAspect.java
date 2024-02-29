package com.example;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Slf4j
@Aspect
public class LogMethodExecutionTimeAspect {

    @Around("@annotation(LogMethodExecutionTime)")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        String nameOfController = getNameController(joinPoint.getSignature().toString());
        log.info("Method " + joinPoint.getSignature().getName() + " in controller " + nameOfController + " start.");
        final long start = System.currentTimeMillis();
        final Object proceed = joinPoint.proceed();
        final long executionTime = System.currentTimeMillis() - start;
        log.info("Method " + joinPoint.getSignature().getName() + " in controller " + nameOfController + " stop.");
        log.info("Method " + joinPoint.getSignature().getName() + " in controller " + nameOfController + " executed in " + executionTime + "ms");
        return proceed;
    }

    private String getNameController(String signature){
        String[] elements = signature.split("\\.");
        String nameOfController = "";
        for (String element : elements) {
            if (element.contains("Controller")) {
                nameOfController = element;
            }
        }
        return nameOfController;
    }
}
