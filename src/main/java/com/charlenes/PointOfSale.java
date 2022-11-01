package com.charlenes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import com.charlenes.helpers.DateTimeHelper;
import com.charlenes.models.Customer;
import com.charlenes.models.Staff;
import com.charlenes.scenarios.ScenarioFactory;
import com.charlenes.models.Extra;
import com.charlenes.models.Order;
import com.charlenes.models.Product;
import com.charlenes.models.ProductType;
import com.charlenes.services.NoSuchOrderException;
import com.charlenes.services.OrderService;
import com.charlenes.services.ProductService;
import com.charlenes.services.SystemService;
import com.charlenes.services.UserService;

public class PointOfSale {

	private static Logger logger = Logger.getLogger(PointOfSale.class.getSimpleName());
	
	private static SystemService systemService;
	private static ProductService productsService;
	private static OrderService orderService;
	private static UserService userService;
	
	private PointOfSale() {}
	
	public static void start() throws Exception {
		systemService = new SystemService();
		orderService = new OrderService(systemService, ScenarioFactory.getStandardScenario());
		productsService = new ProductService();
		userService = new UserService();
				
		logger.info("Starting point of sale system");
		
		populateDatabase();
		logger.info("- Database is populated");
		
		setInvoiceHeaderAndFooter();
		logger.info("- Setting invoice header and footer");
		
		systemService.setSystemLocale("de", "CH");
		logger.info(String.format("- System locale is set to %s", systemService.getSystemLocale().toString()));
		
		orderService.printInvoice(createDummyOrder());
		logger.info("- Printing invoice");
	}
	
	public static void setInvoiceHeaderAndFooter() {
		String merchantName = "C h a r l e n e ' s";
		String merchantExtra = "Coffee Shop";
		String merchantAddress = "https://www.github.com/";
		
		String contactInfo = "Thank you for your visit!";
					
		List<String> header = new ArrayList<>();
		header.add(merchantName);
		header.add(merchantExtra);
		header.add(merchantAddress);
		systemService.setInvoiceHeader(header);
		
		List<String> footer = new ArrayList<>();
		footer.add(contactInfo);
		systemService.setInvoiceFooter(footer);	
	}

	private static void populateDatabase() {
		productsService.save(new Product("Coffee small", "", 2.50, 1, 0.0, ProductType.BEVERAGE));
		productsService.save(new Product("Coffee medium", "", 3.00, 1, 0.0, ProductType.BEVERAGE));
		productsService.save(new Product("Coffee large", "", 3.50, 1, 0.0, ProductType.BEVERAGE));
		productsService.save(new Product("Bacon roll", "", 4.50, 1, 0.0, ProductType.SNACK));
		productsService.save(new Product("Orange juice 0.25l", "Freshly squeezed orange", 3.95, 1, 0.0, ProductType.BEVERAGE));
		
		Optional<Product> coffeeSmallOptional = productsService.getByName("Coffee small");
		if (coffeeSmallOptional.isPresent()) {
			addStandardExtras(coffeeSmallOptional.get());
		}
		
		Optional<Product> coffeeMediumOptional = productsService.getByName("Coffee medium");
		if (coffeeMediumOptional.isPresent()) {
			addStandardExtras(coffeeMediumOptional.get());
		}
		
		Optional<Product> coffeeLargeOptional = productsService.getByName("Coffee large");
		if (coffeeLargeOptional.isPresent()) {
			addStandardExtras(coffeeLargeOptional.get());
		}
	}
	
	private static void addStandardExtras(Product product) {
		if (product != null) {
			product.addExtra(new Extra("Extra milk", "", 0.30, 1, 0.0));
			product.addExtra(new Extra("Foamed milk", "", 0.50, 1, 0.0));
			product.addExtra(new Extra("Special roast coffee", "", 0.90, 1, 0.0));
		}
	}
	
	private static Order createDummyOrder() throws NoSuchOrderException {
		Customer customer = userService.getCustomerSingleton("Joe", "");
		Staff employee = userService.getEmployeeSingleton("Mary", "");
		
		Product coffee = new Product("Coffee", "", 3.00, 2, 0.25, ProductType.BEVERAGE);
		Product baconRoll = new Product("Bacon roll", "", 4.50, 1, 0.0, ProductType.SNACK);
		Order newOrder = new Order(customer, employee, DateTimeHelper.getOffsetDateTime());
		newOrder.setScenarioCallback(ScenarioFactory.getStandardScenario());
		
		Order order = orderService.save(newOrder);	
		order.addProduct(coffee);
		order.addProduct(baconRoll);
		order = orderService.save(order);
		orderService.settle(order);
		
		Order settledOrder = orderService.findById(order.getId()).get();
		return settledOrder;
	}
	
}
