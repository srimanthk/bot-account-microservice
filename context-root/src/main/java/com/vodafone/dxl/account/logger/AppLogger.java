package com.vodafone.dxl.account.logger;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.CodeSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor(onConstructor = @__({ @Autowired }))
@Aspect
@Slf4j
public class AppLogger {

	@Before("execution(* *(..)) && ( within(com.vodafone.dxl.account..*) ) ")
	public void beforeMethod(JoinPoint pjp) {
		StringBuilder logValues = new StringBuilder();
		log.info("Entering into " + pjp.getSignature().getDeclaringTypeName() + " : " + pjp.getSignature().getName());
		if (pjp.getArgs() != null && pjp.getArgs().length > 0) {
			Object getargs[] = pjp.getArgs();
			CodeSignature codeSignature = (CodeSignature) pjp.getSignature();
			String[] argNames = codeSignature.getParameterNames();
			int i = 0;
			for (String arg : argNames) {
				logValues.append(arg + " :" + getargs[i] + " ,");
				i++;
			}
		}
		log.info("Arguments:Values : " + "[" + logValues + "]");
	}

	@After("execution(* *(..)) && ( within(com.vodafone.dxl.account..*) ) ")
	public void afterMethod(JoinPoint pjp) {
		log.info("Exiting from " + pjp.getSignature().getDeclaringTypeName() + " : " + pjp.getSignature().getName());

	}

}
