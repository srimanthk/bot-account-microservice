/**
 * 
 */
package com.vodafone.dxl.account.client;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import org.springframework.ws.soap.client.SoapFaultClientException;

import za.co.vodacom.subscriber.account.SubscriberAccountManagementFault;
import za.co.vodacom.subscriber.account.types.BalanceQueryResponseType;
import za.co.vodacom.subscriber.account.types.BalanceQueryType;

/**
 * <h1>AccountClient</h1>
 * <h5>The account client class communicates with za backend and provides java
 * soap response object.</h5>
 * 
 * @author anupam.pal
 *
 */
@Component
public class AccountClient extends WebServiceGatewaySupport {

	@Autowired
	WebServiceTemplate webServiceTemplate;

	/**
	 * Method which forms soap header and calls za end point , converts the
	 * response to JAXB response java object.
	 * 
	 * @param request
	 * @return
	 * @throws SubscriberAccountManagementFault 
	 * @throws JAXBException 
	 */
	@SuppressWarnings("unchecked")
	public JAXBElement<BalanceQueryResponseType> getBalanceQuery(JAXBElement<BalanceQueryType> request) throws SubscriberAccountManagementFault, JAXBException {
		try {
			return ((JAXBElement<BalanceQueryResponseType>) webServiceTemplate.marshalSendAndReceive(request));
		}
		catch(SoapFaultClientException exp) {
			throw new SubscriberAccountManagementFault(exp.getMessage());
		}catch(Exception exp) {
			throw exp;
		}
	}
}
