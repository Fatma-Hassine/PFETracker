package com.pfetracker.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    @Pointcut("execution(* com.pfetracker.controller..*(..))")
    public void controllerMethods() {}

    @Pointcut("execution(* com.pfetracker.service..*(..))")
    public void serviceMethods() {}

    @Pointcut("execution(* com.pfetracker.repository..*(..))")
    public void repositoryMethods() {}

    @Pointcut("controllerMethods() || serviceMethods() || repositoryMethods()")
    public void applicationMethods() {}

    @Before("applicationMethods()")
    public void logBefore(JoinPoint joinPoint) {
        logger.info("Entrée méthode : {}.{}()",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName());
    }

    @AfterReturning(pointcut = "applicationMethods()", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        logger.info("Méthode exécutée avec succès : {}.{}()",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName());
    }

    @AfterThrowing(pointcut = "applicationMethods()", throwing = "exception")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable exception) {
        logger.error("Exception dans : {}.{}() | Message : {}",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName(),
                exception.getMessage());
    }

    @After("applicationMethods()")
    public void logAfter(JoinPoint joinPoint) {
        logger.info("Sortie méthode : {}.{}()",
                joinPoint.getSignature().getDeclaringTypeName(),
                joinPoint.getSignature().getName());
    }

    @Around("applicationMethods()")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();

        try {
            Object result = joinPoint.proceed();

            long executionTime = System.currentTimeMillis() - startTime;

            logger.info("Temps d'exécution : {}.{}() = {} ms",
                    joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName(),
                    executionTime);

            return result;
        } catch (Throwable exception) {
            long executionTime = System.currentTimeMillis() - startTime;

            logger.error("Erreur après {} ms dans : {}.{}()",
                    executionTime,
                    joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName());

            throw exception;
        }
    }
}