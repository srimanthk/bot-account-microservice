/**
 * 
 */
package com.vodafone.dxl.account.stub;

import java.io.IOException;

import javax.xml.bind.JAXBException;
import javax.xml.soap.SOAPException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.vodafone.dxl.entitymodel.util.DxlUtil;

import lombok.RequiredArgsConstructor;

/**
 * <h1> AccountCSMClientStub </h1>
 * <h5> The AccountCSMClientStub provides stub , we can use this stub if za endpoint is down.  
 * 
 * @author anupam.pal
 *
 */
@Component
@RequiredArgsConstructor(onConstructor = @__({ @Autowired }))
public class AccountCSMClientStub {
	/**
	 * This method un-marshal an XML InputStream object and returns the
	 * corresponding java object and returns mock response.
	 * 
	 * @param request
	 * @return
	 * @throws JAXBException
	 * @throws SOAPException
	 * @throws IOException
	 */
	public Object getBalanceQuery(final Object request) throws JAXBException, IOException, SOAPException {
		/** Ignoring "request" object for mocking response object */
		return DxlUtil.getSoapResponseObject("stub/QueryBalanceResponse.xml",
				Object.class);/** Returning mock response. */
	}
}
