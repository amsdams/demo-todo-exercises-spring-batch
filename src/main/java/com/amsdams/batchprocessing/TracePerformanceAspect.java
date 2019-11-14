package com.amsdams.batchprocessing;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Aspect
@Component
@Slf4j
public class TracePerformanceAspect {

	@Around("execution(* com.amsdams..*.*(..)))")
	public Object logTracePerformanceAspect(ProceedingJoinPoint joinPoint) throws Throwable {

		MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();

		// Get intercepted method details
		String className = methodSignature.getDeclaringType().getSimpleName();
		String methodName = methodSignature.getName();

		long start = System.currentTimeMillis();

		Object result = joinPoint.proceed();
		long end = System.currentTimeMillis();

		// Log method execution time
		log.info("Execution time of " + className + "." + methodName + " :: " + (end - start) + " ms");

		return result;
	}
}
