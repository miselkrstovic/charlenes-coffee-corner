package com.charlenes.models;

public class Customer extends Entity {

	private Long id;
	private String name;
	private String description;

	public Customer() {}

	public Customer(String name, String description) {
		if (name == null || name.isBlank()) throw new IllegalArgumentException();
		
		this.name = name.trim();
		this.description = description;
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

}
