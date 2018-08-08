package com.vodafone.dxl.account.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.JAXBException;
import javax.xml.soap.SOAPException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vodafone.dxl.account.service.AccountService;
import com.vodafone.dxl.entitymodel.dto.EntityObject;

import lombok.RequiredArgsConstructor;
import za.co.vodacom.subscriber.account.SubscriberAccountManagementFault;

/**
 * <h1>AccountController</h1>
 * <h5>The AccountController is a controller class ,which handles all the http
 * request and response for this application. Communicates with service layer to
 * map the mapping service response to Entity object.</h5>
 * 
 * @version 0.0.1
 *
 */
@RestController
@RequiredArgsConstructor(onConstructor = @__({ @Autowired }))
@RequestMapping(value = "/accounts/v1")
public class AccountController {
	/**
	 * Injecting service.
	 */
	@Autowired
	private final AccountService service;

	/**
	 * Method which communicates with AccountService to get the Entity object.
	 * 
	 * @param msisdn
	 * @return
	 * @throws JAXBException
	 * @throws IOException
	 * @throws SOAPException
	 * @throws SubscriberAccountManagementFault
	 */
	@RequestMapping(value = "/balanceQuery", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
	public EntityObject getBalanceQuery(final HttpServletRequest httpServletrequest,
			@RequestParam(name = "msisdn") String msisdn)
			throws JAXBException, IOException, SOAPException, SubscriberAccountManagementFault {
		return service.getBalanceQuery(msisdn); /** Calling service. */
	}
}
