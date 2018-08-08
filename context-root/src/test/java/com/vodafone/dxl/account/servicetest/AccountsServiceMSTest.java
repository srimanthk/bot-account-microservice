package com.vodafone.dxl.account.servicetest;

import static org.junit.Assert.assertEquals;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.ws.client.WebServiceIOException;
import org.springframework.ws.client.WebServiceTransportException;

import com.vodafone.dxl.account.client.AccountClient;
import com.vodafone.dxl.account.service.AccountService;
import com.vodafone.dxl.account.stub.AccountCSMClientStub;
import com.vodafone.dxl.entitymodel.dto.EntityObject;
import com.vodafone.dxl.exception.custom.NoContentException;
import com.vodafone.dxl.exception.custom.ResourceNotFoundException;
import com.vodafone.gigthree.ulff.kafka.KafkaProperties;

import za.co.vodacom.subscriber.account.SubscriberAccountManagementFault;
import za.co.vodacom.subscriber.account.types.BalanceQueryResponseType;

@RunWith(SpringJUnit4ClassRunner.class)
@TestPropertySource(locations = "classpath:application.yml")
@ContextConfiguration(classes = {AccountService.class, KafkaProperties.class})
public class AccountsServiceMSTest {

	@MockBean
	AccountClient client;

	@Autowired
	AccountService accountService;

	@MockBean
	AccountCSMClientStub clientStub;

	@Test
	public void testCase01_getBalanceQuery_Success() throws Exception {
		JAXBElement<BalanceQueryResponseType> soapResponse = getSoapResponseObjectJax("stub/QueryBalanceResponse.xml",
				BalanceQueryResponseType.class);
		Mockito.when(client.getBalanceQuery(Mockito.any())).thenReturn(soapResponse);
		EntityObject entityObject = accountService.getBalanceQuery("27825506489");
		assertEquals("Cents", entityObject.getEntityList().get(0).getServiceBalance().get(0).getCurrencyID());
	}

	@Test
	public void testCase02_getBalanceQuery_WithOutMSISDN() throws Exception {
		JAXBElement<BalanceQueryResponseType> soapResponse = getSoapResponseObjectJax("stub/QueryBalanceResponse.xml",
				BalanceQueryResponseType.class);
		Mockito.when(client.getBalanceQuery(Mockito.any())).thenReturn(soapResponse);
		EntityObject entityObject = accountService.getBalanceQuery("");
		assertEquals(null, entityObject.getEntityList());
	}

	@Test(expected = Exception.class)
	public void testCase02_getBalanceQuery_ThrowsSoapFaultMSISDN()
			throws JAXBException, IOException, SOAPException, SubscriberAccountManagementFault {
		JAXBElement<BalanceQueryResponseType> soapResponse = getSoapResponseObjectJax(
				"stub/QueryBalanceErrorResponse.xml", BalanceQueryResponseType.class);
		Mockito.when(client.getBalanceQuery(Mockito.any())).thenReturn(soapResponse);
		accountService.getBalanceQuery("27825506489");
	}

	@Test(expected = SubscriberAccountManagementFault.class)
	public void testCase03_getBalanceQuery_ThrowsSubscriberAccountManagementFault()
			throws JAXBException, IOException, SOAPException, SubscriberAccountManagementFault {
		Mockito.when(client.getBalanceQuery(Mockito.any())).thenThrow(new SubscriberAccountManagementFault());
		accountService.getBalanceQuery("27825506489");
	}

	@Test(expected = WebServiceIOException.class)
	public void testCase04_getBalanceQuery_ThrowsWebServiceIOException()
			throws JAXBException, IOException, SOAPException, SubscriberAccountManagementFault {
		Mockito.when(client.getBalanceQuery(Mockito.any())).thenThrow(new WebServiceIOException(null));
		accountService.getBalanceQuery("27825506489");
	}

	@Test(expected = WebServiceTransportException.class)
	public void testCase05_getBalanceQuery_WebServiceTransportException()
			throws JAXBException, IOException, SOAPException, SubscriberAccountManagementFault {
		Mockito.when(client.getBalanceQuery(Mockito.any())).thenThrow(new WebServiceTransportException(null));
		accountService.getBalanceQuery("27825506489");
	}

	@Test(expected = NoContentException.class)
	public void testCase06_getBalanceQuery_NoContentException()
			throws JAXBException, IOException, SOAPException, SubscriberAccountManagementFault {
		Mockito.when(client.getBalanceQuery(Mockito.any())).thenThrow(new NoContentException());
		accountService.getBalanceQuery("27825506489");
	}

	@Test(expected = ResourceNotFoundException.class)
	public void testCase07_getBalanceQuery_ResourceNotFoundException()
			throws JAXBException, IOException, SOAPException, SubscriberAccountManagementFault {
		Mockito.when(client.getBalanceQuery(Mockito.any())).thenThrow(new ResourceNotFoundException());
		accountService.getBalanceQuery("27825506489");
	}

	@SuppressWarnings("unchecked")
	public <T> JAXBElement<T> getSoapResponseObjectJax(final String filePath, final Class<T> responseClass)
			throws JAXBException, IOException, SOAPException {
		/** Ignoring \\"request\\" object for mocking response object */
		T response = null;
		if (filePath != null
				&& responseClass != null)/** Checking null values. */
		{
			ClassPathResource resource = new ClassPathResource(filePath);
			InputStream is = new FileInputStream(resource.getFile());
			SOAPMessage soapResponse = MessageFactory.newInstance().createMessage(null, is);
			JAXBContext jaxbContext = JAXBContext.newInstance(responseClass);
			Unmarshaller unmarshaller = jaxbContext
					.createUnmarshaller();/** Creating un-marshaler. */
			response = (T) unmarshaller.unmarshal(soapResponse.getSOAPBody().extractContentAsDocument(), responseClass);

		}
		return (JAXBElement<T>) response;/** Returning mock response. */
	}
}

@EnableConfigurationProperties
class AccountsServiceMSTestConfiguration {
	@Bean
	public AccountClient accountClient() {
		return new AccountClient();
	}

	@Bean
	public AccountCSMClientStub accountCSMClientStub() {
		return new AccountCSMClientStub();
	}

	@Bean
	public AccountService accountService() {
		return new AccountService(accountClient(), accountCSMClientStub());
	}
}
