package com.charlenes.models;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class Product extends Entity {
	
	private Long id;
	private String name;
	private String description;
	private Double unitPrice;
	private Integer quantity;
	private Double discount;
	private ProductType type;
	private Set<Extra> extras = new HashSet<>();
	  
	public Product() {}
	
	public Product(String name, String description, Double unitPrice, Integer quantity, Double discount, ProductType type) {
		if (name == null || name.isBlank()) throw new IllegalArgumentException();
		if (unitPrice == null) throw new IllegalArgumentException();
		if (quantity == null || quantity <= 0) throw new IllegalArgumentException();
		if (discount == null) throw new IllegalArgumentException();
		if (type == null) throw new IllegalArgumentException();

		this.name = name.trim();
		this.description = description;
		this.unitPrice = unitPrice;
		this.quantity = quantity;
		this.discount = discount;
		this.type = type;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		if (id != null) {
			this.id = id;
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		if (name != null && !name.isBlank()) {
			this.name = name.trim();
		}
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		if (description != null && !description.isBlank()) {
			this.description = description.trim();
		}
	}

	public Double getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(Double unitPrice) {
		if (unitPrice != null) {
			this.unitPrice = unitPrice;
		}
	}
	
	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public Double getDiscount() {
		return discount;
	}

	public void setDiscount(Double discount) {
		this.discount = discount;
	}

	public ProductType getType() {
		return type;
	}

	public void setType(ProductType type) {
		if (type != null) {
			this.type = type;
		}
	}

	public Product with(Extra extra) {
		addExtra(extra);
		return this;
	}
	
	public void addExtra(Extra extra) {
		if (extra.getId() == null) {
			extra.setId(extra.getNextId());
		}
		extras.add(extra);
	}
	
	public Set<Extra> getExtras() {
		return extras;
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
		Product other = (Product) obj;
		return Objects.equals(id, other.id);
	}

}