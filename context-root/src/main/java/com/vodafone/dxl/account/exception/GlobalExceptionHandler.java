package com.vodafone.dxl.account.exception;

import javax.validation.ConstraintViolationException;
/**
 * 
 */
import javax.xml.bind.JAXBException;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.ws.client.WebServiceIOException;
import org.springframework.ws.client.WebServiceTransportException;

import com.vodafone.dxl.dxlcommonerrors.commonerrors.DxlCommonErrors;
import com.vodafone.dxl.dxlcommonerrors.model.CommonErrorModel;
import com.vodafone.dxl.entitymodel.util.DxlUtil;
import com.vodafone.dxl.exception.custom.NoContentException;
import com.vodafone.dxl.exception.custom.ResourceNotFoundException;

import lombok.extern.slf4j.Slf4j;
import za.co.vodacom.subscriber.account.SubscriberAccountManagementFault;

/**
 * <h1>GlobalExceptionHandler</h1>
 * <h3>The GlobalExceptionHandler is a exception handler class , which sets the
 * Error model when error occurs in application.</h5>
 * 
 * @author Sathishkumar
 * @version 0.0.1
 *
 */
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
	
	/**
	 * Method which sets CommonErrorModel for error code 500.
	 * 
	 * @param ex
	 * @param request
	 * @return
	 * @throws JAXBException
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<CommonErrorModel> handleGlobalException(Exception ex, WebRequest request)
			throws JAXBException, ClassNotFoundException, InstantiationException, IllegalAccessException {

		log.error("GlobalExceptionHandler.java : handleGlobalException(Exception,WebRequest) : Error : Inside handleGlobalException" + ex.getMessage());
		CommonErrorModel errorModel = DxlCommonErrors.getError("500"); // We need to pass error code here as per business logic. This is under process. 
		return new ResponseEntity<CommonErrorModel>(errorModel, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@ExceptionHandler(SubscriberAccountManagementFault.class)
	public ResponseEntity<CommonErrorModel> handleSubscriberAccountManagementFaultException(SubscriberAccountManagementFault ex, WebRequest request){

		log.error("GlobalExceptionHandler.java : handleSubscriberAccountManagementFaultException(SubscriberAccountManagementFault,WebRequest) : Error : Inside handleSubscriberAccountManagementFaultException" + ex.getMessage());
		CommonErrorModel errorModel = DxlCommonErrors.getError("101"); // We need to pass error code here as per business logic. This is under process.
		if(DxlUtil.isNotNullOrNotEmpty(ex.getMessage()))
			errorModel.setDescription(ex.getMessage());
		return new ResponseEntity<CommonErrorModel>(errorModel, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(WebServiceIOException.class)
	public ResponseEntity<CommonErrorModel> handleWebServiceIOException(WebServiceIOException ex, WebRequest request){
		log.error("GlobalExceptionHandler.java : handleWebServiceIOException(WebServiceIOException,WebRequest) : Error : Inside handleWebServiceIOException" + ex.getMessage());
		CommonErrorModel errorModel = DxlCommonErrors.getError("410"); 
		return new ResponseEntity<CommonErrorModel>(errorModel, HttpStatus.SERVICE_UNAVAILABLE);
	}
	
	@ExceptionHandler(WebServiceTransportException.class)
	public ResponseEntity<CommonErrorModel> handleWebServiceTransportException(WebServiceTransportException ex, WebRequest request){
		log.error("GlobalExceptionHandler.java : handleWebServiceTransportException(WebServiceTransportException,WebRequest) : Error : Inside handleWebServiceTransportException" + ex.getMessage());
		CommonErrorModel errorModel = DxlCommonErrors.getError("401"); 
		return new ResponseEntity<CommonErrorModel>(errorModel, HttpStatus.BAD_GATEWAY);
	}
	
	@ExceptionHandler(NoContentException.class)
	public ResponseEntity<CommonErrorModel> handleNoContentException(NoContentException ex, WebRequest request){

		log.error("GlobalExceptionHandler.java : handleNoContentException(NoContentException,WebRequest) : Error : Inside handleNoContentException" + ex.getMessage());
		CommonErrorModel errorModel = DxlCommonErrors.getError("101");
		return new ResponseEntity<CommonErrorModel>(errorModel, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<CommonErrorModel> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request){

		log.error("GlobalExceptionHandler.java : handleResourceNotFoundException(ResourceNotFoundException,WebRequest) : Error : Inside handleResourceNotFoundException" + ex.getMessage());
		CommonErrorModel errorModel = DxlCommonErrors.getError("190"); 
		return new ResponseEntity<CommonErrorModel>(errorModel, HttpStatus.NOT_FOUND);
	}
	
	@Override
	public ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		log.error("GlobalExceptionHandler.java : handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException,WebRequest) : Error : Inside handleHttpRequestMethodNotSupported" + ex.getMessage());
		CommonErrorModel errorModel = DxlCommonErrors.getError("100"); 
		return new ResponseEntity<Object>(errorModel, HttpStatus.METHOD_NOT_ALLOWED);
	}
	
	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<CommonErrorModel> handleConstraintViolationException(ConstraintViolationException ex, WebRequest request){
		log.error("GlobalExceptionHandler.java : handleConstraintViolationException(ConstraintViolationException,WebRequest) : Error : Inside handleConstraintViolationException" + ex.getMessage());
		CommonErrorModel errorModel = DxlCommonErrors.getError("101"); 
		errorModel.setDescription(ex.getMessage());
		return new ResponseEntity<CommonErrorModel>(errorModel, HttpStatus.BAD_REQUEST);
	}
}
