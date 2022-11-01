package com.charlenes.services;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.charlenes.helpers.DateTimeHelper;
import com.charlenes.models.Customer;
import com.charlenes.models.Extra;
import com.charlenes.models.Staff;
import com.charlenes.scenarios.ScenarioCallback;
import com.charlenes.scenarios.ScenarioFactory;
import com.charlenes.models.Order;
import com.charlenes.models.Product;
import com.charlenes.models.ProductType;
import com.charlenes.services.OrderService.Filter;

class OrderServiceTest {
	
	private SystemService systemService;
	private OrderService orderService;
	private UserService userService;
	
	private Customer customer;
	private Staff employee;
	
	private Product coffee, baconRoll;
	private Extra extraMilk, cinnamon;
	
	@BeforeEach
	void setUp() {
		systemService = new SystemService();
		orderService = new OrderService(systemService, ScenarioFactory.getStandardScenario());
		userService = new UserService();
		
		customer = userService.getCustomerSingleton("Joe", "");
		employee = userService.getEmployeeSingleton("Mary", "");
		
		coffee = new Product("Coffee", "", 3.00, 1, 0.0, ProductType.BEVERAGE);
		baconRoll = new Product("Bacon roll", "", 4.50, 1, 0.0, ProductType.SNACK);
		extraMilk = new Extra("Extra milk", "", 0.25, 1, 0.0);
		cinnamon = new Extra("Cinnamon", "", 0.25, 1, 0.0);
	}
	
	@AfterEach
	void tearDown() {

	}
	
	@Test
	void orderService_createOrder() {
		// Setup
		Order newOrder = new Order(customer, employee, DateTimeHelper.getOffsetDateTime());
		newOrder.setScenarioCallback(ScenarioFactory.getStandardScenario());
		
		// Execution
		Order order = orderService.save(newOrder);
		
		// Validation
		assertNotNull(order);
	}
	
	@Test
	void orderService_viewOrder() {
		// Setup
		Order newOrder = new Order(customer, employee, DateTimeHelper.getOffsetDateTime());
		newOrder.setScenarioCallback(ScenarioFactory.getStandardScenario());
		
		// Execution
		Order order = orderService.save(newOrder);
		order.addProduct(coffee);
		order.addProduct(baconRoll);
		order = orderService.save(order);
		
		Order updatedOrder = orderService.findById(order.getId()).get();

		// Validation
		assertNotNull(updatedOrder);
		assertNotNull(updatedOrder.getProducts());
		assertEquals(2, updatedOrder.getProducts().size());;
		assertEquals("Coffee", updatedOrder.getProducts().get(0).getName());
		assertEquals("Bacon roll", updatedOrder.getProducts().get(1).getName());
	}
	
	@Test
	void orderService_modifyOrder() {
		// Setup
		Order newOrder = new Order(customer, employee, DateTimeHelper.getOffsetDateTime());
		newOrder.setScenarioCallback(ScenarioFactory.getStandardScenario());
		
		// Execution
		Order order = orderService.save(newOrder);	
		order.addProduct(coffee);
		order.addProduct(baconRoll);
		order = orderService.save(order);
		
		Order updatedOrder = orderService.findById(order.getId()).get();
		
		Product product = updatedOrder.getProductById(baconRoll.getId()).get();
		product.setName("French toast");

		// Validation
		assertNotNull(updatedOrder);
		assertNotNull(updatedOrder.getProducts());
		assertEquals(2, updatedOrder.getProducts().size());;
		assertEquals("Coffee", updatedOrder.getProducts().get(0).getName());
		assertEquals("French toast", updatedOrder.getProducts().get(1).getName());
	}
	
	@Test
	void orderService_deleteOrder() {
		// Setup
		Order newOrder = new Order(customer, employee, DateTimeHelper.getOffsetDateTime());
		newOrder.setScenarioCallback(ScenarioFactory.getStandardScenario());
		
		// Execution
		Order order = orderService.save(newOrder);
		orderService.delete(order);

		// Validation
		assertArrayEquals(orderService.findAll().toArray(), 
				new Order[] {});	
	}

	@Test
	void orderService_settleOrder() throws NoSuchOrderException {
		// Setup
		Order newOrder = new Order(customer, employee, DateTimeHelper.getOffsetDateTime());
		newOrder.setScenarioCallback(ScenarioFactory.getStandardScenario());
		
		// Execution
		Order order = orderService.save(newOrder);
		orderService.settle(order);
		
		Order settledOrder = orderService.findById(order.getId()).get();

		// Validation
		assertNotNull(settledOrder);
		assertEquals(true, orderService.isSettled(settledOrder));
	}
		
	@Test
	void orderService_printOrder() throws NoSuchOrderException {
		// Setup
		Order newOrder = new Order(customer, employee, DateTimeHelper.getOffsetDateTime());
		newOrder.setScenarioCallback(ScenarioFactory.getStandardScenario());
		
		// Execution
		Order order = orderService.save(newOrder);
		order.addProduct(coffee);
		order.addProduct(baconRoll);
		order = orderService.save(order);
		orderService.settle(order);
		
		Order settledOrder = orderService.findById(order.getId()).get();
		String invoiceStr = orderService.invoiceToStr(settledOrder);

		// Validation
		assertNotNull(settledOrder);
		assertEquals(true, orderService.isSettled(settledOrder));
		assertEquals(Double.valueOf(7.50), settledOrder.getTotal());
		assertNotNull(invoiceStr);
	}
	
	@Test
	void orderService_freeDrinkScenario() throws NoSuchOrderException {
		// Setup
		Order newOrder = new Order(customer, employee, DateTimeHelper.getOffsetDateTime());
		newOrder.setScenarioCallback(ScenarioFactory.getStandardScenario());

		// Execution
		Order order = orderService.save(newOrder);
		order.addProduct(new Product("Coffee", "", 3.00, 1, 0.0, ProductType.BEVERAGE));
		order.addProduct(new Product("Coffee", "", 3.00, 1, 0.0, ProductType.BEVERAGE));
		order.addProduct(new Product("Coffee", "", 3.00, 3, 0.0, ProductType.BEVERAGE).with(extraMilk));
		order.addProduct(new Product("Orange juice 0.25l", "Freshly squeezed orange", 3.95, 1, 0.0, ProductType.BEVERAGE));		
		order = orderService.save(order);
		orderService.settle(order);

		Order settledOrder = orderService.findById(order.getId()).get();
		String invoiceStr = orderService.invoiceToStr(settledOrder);

		orderService.printInvoice(settledOrder);
		
		// Validation
		assertNotNull(settledOrder);
		assertEquals(true, orderService.isSettled(settledOrder));
		assertEquals(Double.valueOf(16.20), settledOrder.getTotal());
		assertNotNull(invoiceStr);
	}

	@Test
	void orderService_freeExtraScenario() throws NoSuchOrderException {
		// Setup
		Order newOrder = new Order(customer, employee, DateTimeHelper.getOffsetDateTime());
		newOrder.setScenarioCallback(ScenarioFactory.getStandardScenario());
		
		// Execution
		Order order = orderService.save(newOrder);
		order.addProduct(coffee.with(extraMilk).with(cinnamon));
		order.addProduct(baconRoll);
		order = orderService.save(order);
		orderService.settle(order);
		
		Order settledOrder = orderService.findById(order.getId()).get();
		String invoiceStr = orderService.invoiceToStr(settledOrder);

		orderService.printInvoice(settledOrder);

		// Validation
		assertNotNull(settledOrder);
		assertEquals(true, orderService.isSettled(settledOrder));
		assertEquals(Double.valueOf(7.75), settledOrder.getTotal());
		assertNotNull(invoiceStr);
	}
	
	@Test
	void orderService_orderFilter_showAll() throws NoSuchOrderException {
		// Setup
		Order settledOrder = new Order(customer, employee, DateTimeHelper.getOffsetDateTime());
		settledOrder.setScenarioCallback(ScenarioFactory.getStandardScenario());
		Order unsettledOrder = new Order(customer, employee, DateTimeHelper.getOffsetDateTime());
		unsettledOrder.setScenarioCallback(ScenarioFactory.getStandardScenario());
		
		// Execution
		orderService.save(settledOrder);
		orderService.save(unsettledOrder);
		
		orderService.settle(settledOrder);
		List<Order> filteredOrders = orderService.findAll();
		
		// Validation
		assertEquals(2, filteredOrders.size());
		assertEquals(settledOrder.getId(), filteredOrders.get(0).getId());
		assertEquals(unsettledOrder.getId(), filteredOrders.get(1).getId());
	}

	@Test
	void orderService_orderFilter_showSettled() throws NoSuchOrderException {
		// Setup
		Order settledOrder = new Order(customer, employee, DateTimeHelper.getOffsetDateTime());
		settledOrder.setScenarioCallback(ScenarioFactory.getStandardScenario());
		Order unsettledOrder = new Order(customer, employee, DateTimeHelper.getOffsetDateTime());
		unsettledOrder.setScenarioCallback(ScenarioFactory.getStandardScenario());
		
		// Execution
		orderService.save(settledOrder);
		orderService.save(unsettledOrder);
		
		orderService.settle(settledOrder);
		List<Order> filteredOrders = orderService.findAll(Filter.SETTLED);
		
		// Validation
		assertEquals(1, filteredOrders.size());
		assertEquals(settledOrder.getId(), filteredOrders.get(0).getId());
	}

	@Test
	void orderService_orderFilter_showUnsettled() throws NoSuchOrderException {
		// Setup
		Order settledOrder = new Order(customer, employee, DateTimeHelper.getOffsetDateTime());
		settledOrder.setScenarioCallback(ScenarioFactory.getStandardScenario());
		Order unsettledOrder = new Order(customer, employee, DateTimeHelper.getOffsetDateTime());
		unsettledOrder.setScenarioCallback(ScenarioFactory.getStandardScenario());
		
		// Execution
		orderService.save(settledOrder);
		orderService.save(unsettledOrder);
		
		orderService.settle(settledOrder);
		List<Order> filteredOrders = orderService.findAll(Filter.UNSETTLED);
		
		// Validation
		assertEquals(1, filteredOrders.size());
		assertEquals(unsettledOrder.getId(), filteredOrders.get(0).getId());
	}
	
}
