package com.vodafone.dxl.account.configuration;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class CustomObjectMapper extends ObjectMapper{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public CustomObjectMapper() {
		this.setSerializationInclusion(Include.NON_EMPTY);
	}
}
