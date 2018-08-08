package com.vodafone.dxl.account.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;
import javax.xml.soap.SOAPException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.vodafone.dxl.account.client.AccountClient;
import com.vodafone.dxl.account.stub.AccountCSMClientStub;
import com.vodafone.dxl.entitymodel.dto.Entity;
import com.vodafone.dxl.entitymodel.dto.EntityObject;
import com.vodafone.dxl.entitymodel.dto.ServiceBalance;
import com.vodafone.dxl.entitymodel.util.DxlUtil;

import lombok.RequiredArgsConstructor;
import za.co.vodacom.subscriber.account.SubscriberAccountManagementFault;
import za.co.vodacom.subscriber.account.types.BalanceQueryResponseType;
import za.co.vodacom.subscriber.account.types.BundleBalanceType;

/**
 * <h1> AccountService </h1>
 * <h5> The AccountService does the mapping with Entity object for the received JAVA soap response object.</h5>
 * 
 * @author Sathishkumar
 * @version 0.0.1
 *
 */
@Service
@RequiredArgsConstructor(onConstructor = @__({ @Autowired }))
public class AccountService {
	/**
	 * Injecting client.
	 */
	@Value("${client.stub-enabled}")
	private String stubEnabled;
	private final AccountClient client;
	private final AccountCSMClientStub clientStub;
  
	/**
	 * Method which pass msisdn to the account client to get the java soap response object. The java soap response object then mapped to entity object.
	 * 
	 * @param msidn
	 * @return
	 * @throws JAXBException
	 * @throws IOException
	 * @throws SOAPException
	 * @throws SubscriberAccountManagementFault 
	 */
	public EntityObject getBalanceQuery(String msidn) throws JAXBException, IOException, SOAPException, SubscriberAccountManagementFault {
		EntityObject entityObject = new EntityObject();
		if (DxlUtil.isNotNullOrNotEmpty(msidn)) {
			za.co.vodacom.subscriber.account.types.BalanceQueryType balanceQueryType = new za.co.vodacom.subscriber.account.types.BalanceQueryType();
			balanceQueryType.setMSISDN(msidn);
			BalanceQueryResponseType soapResponse = null;
			if (Boolean.valueOf(stubEnabled)) {
				soapResponse = (BalanceQueryResponseType) clientStub.getBalanceQuery(balanceQueryType);
			} else {
				za.co.vodacom.subscriber.account.types.ObjectFactory objectFactory = new za.co.vodacom.subscriber.account.types.ObjectFactory();
				
				//Calls account client to get soap response.
				soapResponse = client.getBalanceQuery(objectFactory.createBalanceQuery(balanceQueryType)).getValue();
				Entity entity = new Entity();
				List<Entity> entityList = new ArrayList<>();
				List<ServiceBalance> serviceBalanceList = new ArrayList<>();
				
				// BundleBalanceType list is iterated to set value into ServiceBalance. 
				for (BundleBalanceType bundleBalanceType : soapResponse.getBundleBalance()) {
					ServiceBalance serviceBalance = new ServiceBalance();
					serviceBalance.setBalanceAmountAmount(BigDecimal.valueOf(bundleBalanceType.getBalance()));
					serviceBalance.setType("Monetary");
					serviceBalance.setBalanceAmountType("RemainingMoney");
					serviceBalance.setCurrencyID(bundleBalanceType.getUoM().value());
					serviceBalance.setCustomerFacingServiceID(String.valueOf(bundleBalanceType.getBundleId()));
					serviceBalance.setCustomerFacingServiceType(bundleBalanceType.getBundleType().value());
					serviceBalanceList.add(serviceBalance);
				}
				//Service balance added to entity.
				entity.setServiceBalance(serviceBalanceList);
				entityList.add(entity);
				entityObject.setEntityList(entityList);
			}
		}
		return entityObject;
	}
}
