package com.charlenes.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.charlenes.models.Extra;
import com.charlenes.models.Product;
import com.charlenes.models.ProductType;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Optional;

import org.junit.jupiter.api.AfterEach;

class ProductServiceTest {

	private ProductService productService;

	@BeforeEach
	void setUp() {
		productService = new ProductService();
	}
	
	@AfterEach
	void tearDown() {
		
	}

	@Test
	void productService_addProduct() {
		// Setup
		Product croissant = new Product("Croissant", "", 1.82, 1, 0.0, ProductType.SNACK);
		
		// Execution
		Product product = productService.save(croissant);

		// Validation
		assertNotNull(product);
	}
	
	@Test
	void productService_retrieveProduct() {
		// Setup
		Product croissant = new Product("Croissant", "", 1.82, 1, 0.0, ProductType.SNACK);
		
		// Execution
		Product product = productService.save(croissant);
		Optional<Product> optional = productService.findById(product.getId());

		// Validation
		assertNotNull(optional);
		assertNotNull(optional.get());
		assertEquals(product.getId(), optional.get().getId());
	}
	
	@Test
	void productService_modifyProduct() {
		// Setup
		Product croissant = new Product("Croissant", "", 1.82, 1, 0.0, ProductType.SNACK);
		
		// Execution
		Product product = productService.save(croissant);
		product.setName("Croissant with butter");
		product.setUnitPrice(1.99);
		product = productService.save(product);

		// Validation
		assertEquals("Croissant with butter", product.getName());
		assertEquals(Double.valueOf(1.99), product.getUnitPrice());		
	}
	
	@Test
	void productService_deleteProduct() {
		// Setup
		Product croissant = new Product("Croissant", "", 1.82, 1, 0.0, ProductType.SNACK);
		
		// Execution
		Product product = productService.save(croissant);
		productService.delete(product);

		// Validation
		assertArrayEquals(productService.findAll().toArray(), 
				new Product[] {});		
	}
	
	@Test
	void productService_addProducts() {
		// Setup
		Product smallCoffee = new Product("Coffee small", "", 2.50, 1, 0.0, ProductType.BEVERAGE);
		Product mediumCoffee = new Product("Coffee medium", "", 3.00, 1, 0.0, ProductType.BEVERAGE);
		Product largeCoffee = new Product("Coffee large", "", 3.50, 1, 0.0, ProductType.BEVERAGE);
		Product baconRoll = new Product("Bacon roll", "", 4.50, 1, 0.0, ProductType.SNACK);
		Product orangeJuice = new Product("Orange juice 0.25l", "Freshly squeezed orange", 3.95, 1, 0.0, ProductType.BEVERAGE);

		// Execution
		productService.save(smallCoffee);
		productService.save(mediumCoffee);
		productService.save(largeCoffee);
		productService.save(baconRoll);
		productService.save(orangeJuice);

		// Validation
		assertArrayEquals(productService.findAll().toArray(), 
				new Product[] {smallCoffee, mediumCoffee, largeCoffee, baconRoll, orangeJuice});
	}
	
	@Test
	void productService_addExtrasToProduct() {
		// Setup
		Product product = new Product("Coffee small", "", 2.50, 1, 0.0, ProductType.BEVERAGE);
		Extra extraMilk = new Extra("Extra milk", "", 0.30, 1, 0.0);
		Extra foamedMilk = new Extra("Foamed milk", "", 0.50, 1, 0.0);
		Extra roastCoffee = new Extra("Special roast coffee", "", 0.90, 1, 0.0);

		product.addExtra(extraMilk);
		product.addExtra(foamedMilk);
		product.addExtra(roastCoffee);
		
		// Execution
		productService.save(product);

		// Validation
		assertArrayEquals(productService.findAll().toArray(), 
				new Product[] {product});
		assertArrayEquals(product.getExtras().toArray(),
				new Extra[] {extraMilk, foamedMilk, roastCoffee});
	}
	
}
