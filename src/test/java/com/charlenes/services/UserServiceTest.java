package com.charlenes.services;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.charlenes.models.Customer;
import com.charlenes.models.Staff;

class UserServiceTest {

	private UserService userService;
	
	@BeforeEach
	void setUp() {	
		userService = new UserService();
	}
	
	@AfterEach
	void tearDown() {
		
	}
	
	@Test
	void userService_createCustomer() {
		// Setup
		String name = "Joe";
		String description = "";
		
		// Execution
		Customer customer = userService.getCustomerSingleton(name, description);
		
		// Validation
		assertNotNull(customer);
		assertEquals("Joe", customer.getName());
	}
	
	@Test
	void userService_createEmployee() {
		// Setup
		String name = "Mary";
		String description = "";
		
		// Execution
		Staff employee = userService.getEmployeeSingleton(name, description);

		// Validation
		assertNotNull(employee);
		assertNotNull("Mary", employee.getName());
	}

}
