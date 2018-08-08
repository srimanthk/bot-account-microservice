package com.vodafone.dxl.account.controllertest;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.validation.ConstraintViolationException;
import javax.xml.bind.JAXBException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.ws.client.WebServiceIOException;
import org.springframework.ws.client.WebServiceTransportException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vodafone.dxl.account.application.AccountApplication;
import com.vodafone.dxl.account.controller.AccountController;
import com.vodafone.dxl.account.service.AccountService;
import com.vodafone.dxl.entitymodel.dto.Entity;
import com.vodafone.dxl.entitymodel.dto.EntityObject;
import com.vodafone.dxl.entitymodel.dto.ServiceBalance;
import com.vodafone.dxl.entitymodel.util.DxlUtil;
import com.vodafone.dxl.exception.custom.NoContentException;
import com.vodafone.dxl.exception.custom.ResourceNotFoundException;

import za.co.vodacom.subscriber.account.SubscriberAccountManagementFault;
import za.co.vodacom.subscriber.account.types.BalanceQueryResponseType;
import za.co.vodacom.subscriber.account.types.BundleBalanceType;

@RunWith(SpringJUnit4ClassRunner.class)
@WebMvcTest(value = AccountController.class, secure = false)
@ContextConfiguration(classes = { AccountApplication.class })
@ComponentScan(basePackages = { "com.vodafone.dxl.account.*" })
public class AccountsControllerMSTest {
	@MockBean
	AccountService service;

	@Autowired
	private MockMvc mockMvc;

	@Test
	public void testCase01_getBalanceQuery_Success() throws Exception {
		String uri = "/accounts/v1/balanceQuery";
		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(uri).param("msisdn", "27828234114")).andReturn();

		assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
	}

	@Test
	public void testCase02_getBalanceQuery_Entity_Success() throws Exception {
		String uri = "/accounts/v1/balanceQuery";
		BalanceQueryResponseType soapResponse = (BalanceQueryResponseType) DxlUtil
				.getSoapResponseObject("stub/QueryBalanceResponse.xml", BalanceQueryResponseType.class);
		EntityObject entityObject = new EntityObject();
		Entity entity = new Entity();
		List<Entity> entityList = new ArrayList<>();
		List<ServiceBalance> serviceBalanceList = new ArrayList<>();
		for (BundleBalanceType bundleBalanceType : soapResponse.getBundleBalance()) {
			ServiceBalance serviceBalance = new ServiceBalance();
			serviceBalance.setBalanceAmountAmount(BigDecimal.valueOf(bundleBalanceType.getBalance()));
			serviceBalance.setType("Monetary");
			serviceBalance.setBalanceAllowanceType("RemainingMoney");
			serviceBalance.setCurrencyID(bundleBalanceType.getUoM().value());
			serviceBalanceList.add(serviceBalance);
		}
		entity.setServiceBalance(serviceBalanceList);
		entityList.add(entity);
		entityObject.setEntityList(entityList);
		Mockito.when(service.getBalanceQuery(Mockito.anyString())).thenReturn(entityObject);

		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(uri).param("msisdn", "27828234114")).andReturn();
		assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
	}

	@Test(expected = Exception.class)
	public void testCase03_getBalanceQuery_Entity_Negative() throws Exception {
		String uri = "/accounts/v1/balanceQuery";
		EntityObject entityObject = new EntityObject();
		List<Entity> entityList = new ArrayList<>();
		entityObject.setEntityList(entityList);
		Mockito.when(service.getBalanceQuery(Mockito.anyString())).thenReturn(entityObject);

		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(uri).param("msisdn", "")).andReturn();
		ObjectMapper objectMapper = new ObjectMapper();
		EntityObject response = objectMapper.readValue(result.getResponse().getContentAsString(), EntityObject.class);
		assertEquals(0, response.getEntityList().size());
	}

	@Test
	public void testCase04_getBalanceQuery_ThrowsSubscriberAccountManagementFault() throws Exception {
		String uri = "/accounts/v1/balanceQuery";
		EntityObject entityObject = new EntityObject();
		List<Entity> entityList = new ArrayList<>();
		entityObject.setEntityList(entityList);
		Mockito.when(service.getBalanceQuery(Mockito.anyString())).thenThrow(new SubscriberAccountManagementFault());

		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(uri).param("msisdn", "27828234114")).andReturn();
		assertEquals(400, result.getResponse().getStatus());
	}

	@Test
	public void testCase05_getBalanceQuery_ThrowsWebServiceIOException() throws Exception {
		String uri = "/accounts/v1/balanceQuery";
		EntityObject entityObject = new EntityObject();
		List<Entity> entityList = new ArrayList<>();
		entityObject.setEntityList(entityList);
		Mockito.when(service.getBalanceQuery(Mockito.anyString())).thenThrow(new WebServiceIOException(null));

		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(uri).param("msisdn", "27828234114")).andReturn();
		assertEquals(503, result.getResponse().getStatus());
	}

	@Test
	public void testCase06_getBalanceQuery_ThrowsWebServiceTransportException1() throws Exception {
		String uri = "/accounts/v1/balanceQuery";
		EntityObject entityObject = new EntityObject();
		List<Entity> entityList = new ArrayList<>();
		entityObject.setEntityList(entityList);
		Mockito.when(service.getBalanceQuery(Mockito.anyString())).thenThrow(new WebServiceTransportException(null));

		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(uri).param("msisdn", "27828234114")).andReturn();
		assertEquals(502, result.getResponse().getStatus());
	}

	@Test
	public void testCase07_getBalanceQuery_ThrowsNoContentException() throws Exception {
		String uri = "/accounts/v1/balanceQuery";
		EntityObject entityObject = new EntityObject();
		List<Entity> entityList = new ArrayList<>();
		entityObject.setEntityList(entityList);
		Mockito.when(service.getBalanceQuery(Mockito.anyString())).thenThrow(new NoContentException());

		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(uri).param("msisdn", "27828234114")).andReturn();
		assertEquals(400, result.getResponse().getStatus());
	}

	@Test
	public void testCase08_getBalanceQuery_ThrowsResourceNotFoundException() throws Exception {
		String uri = "/accounts/v1/balanceQuery";
		EntityObject entityObject = new EntityObject();
		List<Entity> entityList = new ArrayList<>();
		entityObject.setEntityList(entityList);
		Mockito.when(service.getBalanceQuery(Mockito.anyString())).thenThrow(new ResourceNotFoundException());

		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(uri).param("msisdn", "27828234114")).andReturn();
		assertEquals(404, result.getResponse().getStatus());
	}

	@Test
	public void testCase09_getBalanceQuery_ThrowsException() throws Exception {
		String uri = "/accounts/v1/balanceQuery";
		EntityObject entityObject = new EntityObject();
		List<Entity> entityList = new ArrayList<>();
		entityObject.setEntityList(entityList);
		Mockito.when(service.getBalanceQuery(Mockito.anyString())).thenThrow(new JAXBException(""));

		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(uri).param("msisdn", "27828234114")).andReturn();
		assertEquals(500, result.getResponse().getStatus());
	}

	@Test
	public void testCase10_getBalanceQuery_ThrowshandleHttpRequestMethodNotSupported() throws Exception {
		String uri = "/accounts/v1/balanceQuery";
		EntityObject entityObject = new EntityObject();
		List<Entity> entityList = new ArrayList<>();
		entityObject.setEntityList(entityList);
		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post(uri).param("msisdn", "27828234114")).andReturn();
		assertEquals(405, result.getResponse().getStatus());
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void test_Controller_whenConstraintViolationException() throws Exception {
		String uri = "/accounts/v1/balanceQuery";
		Mockito.when(service.getBalanceQuery(Mockito.anyString()))
		.thenThrow(ConstraintViolationException.class);
		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(uri).param("msisdn", "27828234114")).andReturn();
		assertEquals(400, result.getResponse().getStatus());
	}
}
