package com.vodafone.dxl.account.logger;

/**
 * 
 */
import java.time.ZonedDateTime;
import java.util.Objects;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.CodeSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.vodafone.dxl.dxlcommonerrors.commonerrors.CSMErrors;
import com.vodafone.dxl.dxlcommonerrors.commonerrors.DxlCommonErrors;
import com.vodafone.gigthree.ulff.Action;
import com.vodafone.gigthree.ulff.ULFF;
import com.vodafone.gigthree.ulff.ULFFConstants;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author anupam.pal
 *
 */
@Component
@RequiredArgsConstructor(onConstructor = @__({ @Autowired }) )
@Slf4j
@Aspect
public class ULFFLogger {
	private static final String DASH = " - ";

	@Value("${client.default-uri}")
	private String defaultUri;

	Action responseIn;

	/**
	 * 
	 */
	@Pointcut("execution(* com.vodafone.dxl.account.controller.AccountController.*(..))")
	public void adviceController() {
	}

	/**
	 * 
	 */
	@Pointcut("execution(* com.vodafone.dxl.account.exception.GlobalExceptionHandler.*(..))")
	public void adviceExeptionHandler() {
	}

	/**
	 * 
	 * @param pjp
	 * @throws Throwable
	 */
	@Before("adviceController()")
	public void logUlffRequestIn(JoinPoint pjp) throws Throwable {
		ULFF.getCurrentTransaction().setLayer("ms");
		log.info("info: inside ULFFLogger.logUlffResponseIn(JoinPoint)");
		final Action requestIn = ULFF.getCurrentTransaction().getOrCreateInboundRequestAction();
		final Action requestOut = ULFF.getCurrentTransaction().getOrCreateRequestOutAction();
		requestIn.setServiceId(pjp.getSignature().getName());
		requestOut.setServiceId(pjp.getSignature().getName());
		requestOut.setUri(defaultUri);
		requestIn.setTimestamp(ZonedDateTime.now());
		if (pjp.getArgs()[0] instanceof HttpServletRequest) {
			requestIn.setHttpMethod(((HttpServletRequest) pjp.getArgs()[0]).getMethod());
			requestOut.setHttpMethod(((HttpServletRequest) pjp.getArgs()[0]).getMethod());
		}
		if (pjp.getArgs() != null && pjp.getArgs().length > 0) {
			Object getargs[] = pjp.getArgs();
			CodeSignature codeSignature = (CodeSignature) pjp.getSignature();
			String[] argNames = codeSignature.getParameterNames();
			int i = 0;
			for (String arg : argNames) {
				if (arg.equals("msisdn")) {
					ULFF.getCurrentTransaction().setMsisdn(getargs[i].toString());
					ULFF.populateMsisdnAndCountryCode(getargs[i].toString());
					break;
				}
				i++;
			}
		}
	}

	/**
	 * 
	 * @param pjp
	 * @throws Throwable
	 */
	@After("adviceController()")
	public void logUlffResponseOut(JoinPoint pjp) throws Throwable {
		log.info("info: inside ULFFLogger.logUlffResponseOut(JoinPoint)");
		final Action responseOut = ULFF.getCurrentTransaction().getOrCreateOutboundResponseAction();
		final Action requestOut = ULFF.getCurrentTransaction().createOutboundRequestAction();
		final Action requestIn = ULFF.getCurrentTransaction().getOrCreateInboundRequestAction();
		responseIn = ULFF.getCurrentTransaction().createInboundResponseAction();
		responseIn.setServiceId(pjp.getSignature().getName());
		responseOut.setServiceId(pjp.getSignature().getName());
		requestOut.setServiceId(pjp.getSignature().getName());
		responseIn.setStatus(ULFFConstants.STATUS_OK);
		responseOut.setStatus(ULFFConstants.STATUS_OK);
		requestOut.setHeaders(requestIn.getHeaders());
		responseIn.setHeaders(responseOut.getHeaders());
		String corTracking;
		if (Objects.nonNull(requestIn.getCorrelationTracking())) {
			corTracking = ULFF.getCurrentTransaction().getComponent();
		} else {
			corTracking = requestIn.getCorrelationTracking();
		}
		requestIn.setCorrelationTracking(corTracking);
		requestOut.setCorrelationTracking(corTracking);
		requestOut.setUri(defaultUri);
		responseIn.setType(ULFFConstants.LOGPOINT_INBOUND_RESPONSE);
		responseIn.setTimestamp(ZonedDateTime.now());
		if (pjp.getArgs()[0] instanceof HttpServletRequest) {
			responseIn.setHttpMethod(((HttpServletRequest) pjp.getArgs()[0]).getMethod());
			responseOut.setHttpMethod(((HttpServletRequest) pjp.getArgs()[0]).getMethod());
			requestOut.setHttpMethod(((HttpServletRequest) pjp.getArgs()[0]).getMethod());
		}
	}

	/**
	 * This is invoked if any runtime exception occurs and to capture.
	 * 
	 * @param pjp
	 * @throws Throwable
	 */
	@After("adviceExeptionHandler()")
	public void logUlffResponseOutForException(JoinPoint pjp) throws Throwable {
		// final Action responseIn =
		// ULFF.getCurrentTransaction().createInboundResponseAction();
		final Action responseOut = ULFF.getCurrentTransaction().getOrCreateOutboundResponseAction();
		responseIn.setServiceId("getBalanceQuery");
		responseOut.setServiceId("getBalanceQuery");
		responseIn.setTimestamp(ZonedDateTime.now());
		responseIn.setErrorCode(DxlCommonErrors.getCurrentError().getErrorCode());
		responseIn.setError(DxlCommonErrors.getCurrentError().getName());
		responseIn.setErrorMessage(DxlCommonErrors.getCurrentError().getDescription());
		responseIn.setStatus("error");
		responseOut.setStatus("error");
	}
}
