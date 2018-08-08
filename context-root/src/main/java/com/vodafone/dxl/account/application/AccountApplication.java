package com.vodafone.dxl.account.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;



/**
 * <h1>AccountApplication</h1>
 * <h5>The AccountApplication is a spring boot starter class , which bootstraps
 * the application.This application communicates with ZA backend and converts
 * the SOAP response to Java Objects , The converted java object then mapped to
 * Entity object as per mapping given</h5>
 * 
 * @author Sathishkumar
 * @version 0.0.1
 *
 */
@SpringBootApplication
@ComponentScan(basePackages = { "com.vodafone.dxl.account.*" })
public class AccountApplication {

	
	/**
	 * Method bootstraps application.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		SpringApplication.run(AccountApplication.class, args);
	}
	
 
}
