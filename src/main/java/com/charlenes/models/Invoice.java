package com.charlenes.models;

import java.time.OffsetDateTime;

public class Invoice extends Entity {

	private String invoiceNumber;
	private Order order;
	private Customer customer;
	private Staff employee;
	private OffsetDateTime invoiceDate;

	public Invoice() {}
	
	public Invoice(Order order, Customer customer, Staff employee, OffsetDateTime invoiceDate) {
		if (order == null) throw new IllegalArgumentException();
		if (customer == null) throw new IllegalArgumentException();
		if (employee == null) throw new IllegalArgumentException();
		if (invoiceDate == null) throw new IllegalArgumentException();

		this.order = order;
		this.customer = customer;
		this.employee = employee;
		this.invoiceDate = invoiceDate;
	}

	public String getInvoiceNumber() {
		return invoiceNumber;
	}

	public void setInvoiceNumber(String invoiceNumber) {
		if (invoiceNumber != null) {
			this.invoiceNumber = invoiceNumber;
		}
	}

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		if (order != null) {
			this.order = order;
		}
	}

	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		if (customer != null) {
			this.customer = customer;
		}
	}

	public Staff getEmployee() {
		return employee;
	}

	public void setEmployee(Staff employee) {
		if (employee != null) {
			this.employee = employee;
		}
	}

	public OffsetDateTime getInvoiceDate() {
		return invoiceDate;
	}

	public void setInvoiceDate(OffsetDateTime invoiceDate) {
		if (invoiceDate != null) {
			this.invoiceDate = invoiceDate;
		}
	}

}
