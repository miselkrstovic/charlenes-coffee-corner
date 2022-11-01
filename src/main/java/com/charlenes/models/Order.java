package com.charlenes.models;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.charlenes.scenarios.ScenarioCallback;

public class Order extends Entity {

	private static Logger logger = Logger.getLogger(Order.class.getSimpleName());
	
	class ManyToMany<S, T> {
		
		private S right;
		private T left;

		public ManyToMany(S right, T left) {
			if (right == null) throw new IllegalArgumentException();
			if (left == null) throw new IllegalArgumentException();

			this.right = right;
			this.left = left;
		}

		public T getItem() {
			return left;
		}

	}
	
	private Long id;
	private Customer customer; 
	private Staff employee;
	private OffsetDateTime orderDate;
	private ScenarioCallback scenarioCallback;
	private List<ManyToMany<Order, Product>> orderProducts = new ArrayList<>();
	
	public Order() {}
	
	public Order(Customer customer, Staff employee, OffsetDateTime orderDate) {
		if (customer == null) throw new IllegalArgumentException();
		if (employee == null) throw new IllegalArgumentException();
		if (orderDate == null) throw new IllegalArgumentException();

		this.customer = customer;
		this.employee = employee;
		this.orderDate = orderDate;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		if (id != null) {
			this.id = id;
		}
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomerId(Customer customer) {
		if (customer != null) {
			this.customer = customer;
		}
	}

	public Staff getEmployee() {
		return employee;
	}

	public void setEmployeeId(Staff employee) {
		if (employee != null) {
			this.employee = employee;
		}
	}

	public OffsetDateTime getOrderDate() {
		return orderDate;
	}

	public void setOrderDate(OffsetDateTime orderDate) {
		if (orderDate != null) {
			this.orderDate = orderDate;
		}
	}

	public void addProduct(Product product) {
		if (product != null ) {
			onBeforeCallback();
			ManyToMany<Order, Product> orderProduct = new ManyToMany(this, product);
			orderProducts.add(orderProduct);
			onAfterCallback();
		}
	}

	public Optional<Product> getProduct(Product product) {
		return orderProducts.stream().filter(op -> op.getItem().equals(product))
				.map(op -> op.getItem()).findFirst();
	}

	public Double getTotal() {
		Double discount = 0d;
		Double total = 0d;
		for (Product product : getProducts()) {
			total += product.getQuantity() * product.getUnitPrice();
			
			Set<Extra> extras = product.getExtras();
			for (Extra extra : extras) {
				total += extra.getQuantity() * extra.getUnitPrice();
				discount += extra.getDiscount();
			}
			discount += product.getDiscount();
		}
		total -= discount;
		
		return total;
	}
	
	public void persist() {
		for (ManyToMany<Order, Product> orderProduct : orderProducts) {
			Product product = orderProduct.getItem();
			if (product != null && (product instanceof Entity)) {
				product.setId(product.getNextId());
			}
			for (Extra extra : product.getExtras()) {
				if (extra != null && (extra instanceof Entity)) {
					extra.setId(extra.getNextId());
				}				
			}
		}
	}
	
	public void clearOrder() {
		onBeforeCallback();
		orderProducts.clear();
		onAfterCallback();
	}

	private final Comparator PRODUCT_COMPARATOR = new Comparator<Product>() {

		@Override
		public int compare(Product o1, Product o2) {
			return o1.getId().compareTo(o2.getId());
		}
		
	};

	public List<Product> getProducts() {
		List<Product> products = orderProducts.stream().map(op -> op.getItem()).collect(Collectors.toList());
		products.sort(PRODUCT_COMPARATOR);
		return products;
	}

	public Optional<Product> getProductById(Long id) {
		Optional<ManyToMany<Order, Product>> orderProductOpt = orderProducts.stream().filter(orderProduct -> orderProduct.getItem().getId()
				.equals(id)).findFirst();
		return Optional.of(orderProductOpt.get().getItem());
	}

	public void onBeforeCallback() {
		if (scenarioCallback != null) {
			try {
				scenarioCallback.onBeforeCurrentOrderChange(this);
			} catch(Exception ex) {
				logger.warning(ex.getMessage());
			}
		}
	}
	
	public void onAfterCallback() {
		if (scenarioCallback != null) {
			try {
				scenarioCallback.onAfterCurrentOrderChange(this);
			} catch(Exception ex) {
				logger.warning(ex.getMessage());
			}
		}
	}	
	
	public void setScenarioCallback(ScenarioCallback scenarioCallback) {
		this.scenarioCallback = scenarioCallback;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Order other = (Order) obj;
		return Objects.equals(id, other.id);
	}

}