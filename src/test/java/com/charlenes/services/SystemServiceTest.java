package com.charlenes.services;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SystemServiceTest {

	private SystemService systemService;
	
	@BeforeEach
	void setUp() {
		systemService = new SystemService();
	}
	
	@AfterEach
	void tearDown() {
		systemService.resetSystemLocale();
		systemService.setInvoiceHeader(new ArrayList<>());
		systemService.setInvoiceFooter(new ArrayList<>());
	}
	
	@Test
	void systemService_testDefaultLocale() {
		// Setup
		double amt = 123.456;
		
		// Execution
		NumberFormat formatter = NumberFormat.getCurrencyInstance();
		
		// Validation
		assertEquals("£123.46", formatter.format(amt));	
	}
	
	@Test
	void systemService_setSystemLocale() {
		// Setup
		double amt = 123.456;
		String language = "de"; 
		String country = "CH";
		
		// Execution
		systemService.setSystemLocale(language, country);
		NumberFormat formatter = NumberFormat.getCurrencyInstance();
		
		// Validation
		assertEquals("CHF 123.46", formatter.format(amt));
	}
	
	@Test
	public void systemService_setInvoiceHeader() {
		// Setup
		String merchantName = "C h a r l e n e ' s";
		String merchantExtra = "Coffee Shop";
		String merchantAddress = "https://www.github.com/";
					
		// Execution
		List<String> header = new ArrayList<>();
		header.add(merchantName);
		header.add(merchantExtra);
		header.add(merchantAddress);
		systemService.setInvoiceHeader(header);
		
		// Validation
		assertNotNull(systemService.getInvoiceHeader());
		assertEquals(header.size(), systemService.getInvoiceHeader().size());
	}
	
	@Test
	public void systemService_setInvoiceFooter() {
		// Setup
		String contactInfo = "Thank you for your visit!";
					
		// Execution
		List<String> footer = new ArrayList<>();
		footer.add(contactInfo);
		systemService.setInvoiceFooter(footer);
		
		// Validation
		assertNotNull(systemService.getInvoiceFooter());
		assertEquals(footer.size(), systemService.getInvoiceFooter().size());		
	}
	
}
