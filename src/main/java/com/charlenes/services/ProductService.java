package com.charlenes.services;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import com.charlenes.models.Entity;
import com.charlenes.models.Extra;
import com.charlenes.models.Product;

public class ProductService {

	private List<Product> products = new ArrayList<>();
	
	public void persist(Product product) {
		for (Extra extra : product.getExtras()) {
			if (extra != null && (extra instanceof Entity)) {
				extra.setId(extra.getNextId());
			}				
		}
	}
	
	public synchronized Product save(Product product) {
		if (product != null && (product instanceof Entity)) {
			if (product.getId() == null) {
				product.setId(product.getNextId());
				products.add(product);
			} else {
				products.set(products.indexOf(product), product);
			}
			persist(product);
			return products.get(products.indexOf(product));
		} else {
			throw new IllegalArgumentException();
		}
	}

	public Optional<Product> findById(Long id) {
		return products.stream().filter(product -> product.getId().compareTo(id) == 0).findFirst();
	}
	
	private final Comparator PRODUCT_COMPARATOR = new Comparator<Product>() {

		@Override
		public int compare(Product o1, Product o2) {
			return o1.getId().compareTo(o2.getId());
		}
		
	};
	
	public List<Product> findAll() {
		return products.stream().sorted(PRODUCT_COMPARATOR).toList();
	}

	public Optional<Product> getByName(String string) {
		return products.stream().filter(product -> product.getName().contains(string)).findFirst();
	}

	public synchronized void delete(Product product) {
		if (product != null) {
			if (products.contains(product)) {
				products.remove(product);
			}
		} else {
			throw new IllegalArgumentException();
		}
	}

}
