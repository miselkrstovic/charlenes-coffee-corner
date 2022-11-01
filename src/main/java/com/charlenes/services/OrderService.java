package com.charlenes.services;

import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.charlenes.helpers.DateTimeHelper;
import com.charlenes.models.Entity;
import com.charlenes.models.Extra;
import com.charlenes.models.Invoice;
import com.charlenes.models.Order;
import com.charlenes.models.Product;
import com.charlenes.scenarios.ScenarioCallback;

public class OrderService {

	private static Logger logger = Logger.getLogger(OrderService.class.getSimpleName());
	
	public enum Filter {
		ALL,
		SETTLED,
		UNSETTLED
	}
	
	private List<Order> orders = new ArrayList<>();
	private List<Invoice> invoices = new ArrayList<>();
	
	private SystemService systemService;
	private ScenarioCallback scenarioCallback;
	
	public OrderService(SystemService system, ScenarioCallback scenarioCallback) {		
		this.systemService = system;
		this.scenarioCallback = scenarioCallback;
	}
	
	public void onBeforeCallback() {
		if (scenarioCallback != null) {
			try {
				scenarioCallback.onBeforeOrdersChange(orders);
			} catch(Exception ex) {
				logger.warning(ex.getMessage());
			}
		}
	}
	
	public void onAfterCallback() {
		if (scenarioCallback != null) {
			try {
				scenarioCallback.onAfterOrdersChange(orders);
			} catch(Exception ex) {
				logger.warning(ex.getMessage());
			}
		}
	}	
	
	public synchronized Order save(Order order) {
		if (order != null && (order instanceof Entity)) {
			onBeforeCallback();
			if (order.getId() == null) {
				order.setId(order.getNextId());	
				orders.add(order);	
			} else {
				orders.set(orders.indexOf(order), order);
			}
			order.persist();
			onAfterCallback();
			
			return orders.get(orders.indexOf(order));
		} else {
			throw new IllegalArgumentException();
		}
	}
	
	public synchronized Invoice save(Invoice invoice) {
		if (invoice != null && (invoice instanceof Entity)) {
			if (invoice.getInvoiceNumber() == null) {
				invoice.setInvoiceNumber(invoice.getNextUuid());
				invoices.add(invoice);
			} else {
				invoices.set(orders.indexOf(invoice), invoice);
			}
			return invoices.get(orders.indexOf(invoice));
		} else {
			throw new IllegalArgumentException();
		}
	}

	public Optional<Order> findById(Long id) {
		return orders.stream().filter(order -> order.getId().compareTo(id) == 0).findFirst();
	}
	
	public List<Order> findAll() {
		return findAll(Filter.ALL);
	}

	private final Comparator<Order> INVOICE_COMPARATOR = new Comparator<>() {

		@Override
		public int compare(Order o1, Order o2) {
			return o1.getId().compareTo(o2.getId());
		}
		
	};
	
	public List<Order> findAll(Filter filter) {
		List<Order> invoicedOrders = invoices.stream().map(i -> i.getOrder()).collect(Collectors.toList());
		switch (filter) {
			case SETTLED:
				return orders.stream().filter(order -> invoicedOrders.contains(order)).sorted(INVOICE_COMPARATOR).toList();
			case UNSETTLED:
				return orders.stream().filter(order -> !invoicedOrders.contains(order)).sorted(INVOICE_COMPARATOR).toList();
			default:
				return orders;
		}
	}
	
	public synchronized void delete(Order order) {
		if (order != null) {
			if (orders.contains(order)) {
				onBeforeCallback();
				order.clearOrder();
				orders.remove(order);
				onBeforeCallback();
			}
		} else {
			throw new IllegalArgumentException();
		}
	}

	public synchronized void settle(Order orderToSettle) throws NoSuchOrderException {
		if (orderToSettle != null) {
			Optional<Order> foundOrder = orders.stream().filter(order -> order.getId().compareTo(orderToSettle.getId()) == 0).findFirst();
			if (foundOrder.isPresent()) {
				Order order = foundOrder.get();
				for (Invoice invoice : invoices) {
					if (invoice.getOrder().getId().equals(order.getId())) {
						return;
					}
				}
				
				Invoice newInvoice = new Invoice(orderToSettle, orderToSettle.getCustomer(), 
						orderToSettle.getEmployee(), DateTimeHelper.getOffsetDateTime());
				newInvoice.setInvoiceNumber(newInvoice.getNextUuid());
				invoices.add(newInvoice);
			} else {
				throw new NoSuchOrderException();
			}
		}
	}

	public boolean isSettled(Order order) {
		for (Invoice invoice : invoices) {
			if (invoice.getOrder().getId().equals(order.getId())) {
				return true;
			}
		}
		return false;
	}

	public String invoiceToStr(Order order) {
		for (Invoice invoice : invoices) {
			if (invoice.getOrder().getId().equals(order.getId())) {
				return invoiceFormat(invoice);
			}
		}
		return null;
	}
	
	public void printInvoice(Order order) {
		if (order != null) {
			System.out.println(invoiceToStr(order));
		}
	}
	
	public String padStr(String line, int maxLen, char chr) {
		if (line != null) { 
			int padWidth = Math.round((maxLen - line.length()) / 2);
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < padWidth; i++) {
				sb.append(chr);
			}
			return sb.toString() + line + sb.toString();
		} else {
			return line;
		}
	}
		
	public String invoiceFormat(Invoice invoice) {
		final int pageWidth = 40;
		
		StringBuilder sb = new StringBuilder();
		sb.append("+----------------------------------------+\n");
		for (String line : systemService.getInvoiceHeader()) {
			sb.append(String.format("|%40s|\n", padStr(line.trim(), pageWidth, ' ')));
		}
		sb.append("|----------------------------------------|\n");
		sb.append("|         C a s h   I n v o i c e        |\n");
		sb.append(String.format("|%40s|\n", padStr(invoice.getInvoiceNumber(), pageWidth, ' ')));
		sb.append(String.format("|%40s|\n", 
				padStr(invoice.getInvoiceDate().format(DateTimeFormatter.RFC_1123_DATE_TIME).toString()
				, pageWidth, ' ')));
		sb.append("|----------------------------------------|\n");
		sb.append(String.format("| %-27s %3s %6s |\n", "", "QTY", "PRICE"));
		sb.append("|                                        |\n");
		
		Double discount = 0d;
		Double total = 0d;
		for (Product product : invoice.getOrder().getProducts()) {
			sb.append(String.format("| %-28s %2d %6.2f |\n", product.getName(), product.getQuantity(), product.getUnitPrice()));
			total += product.getQuantity() * product.getUnitPrice();
			
			Set<Extra> extras = product.getExtras();
			for (Extra extra : extras) {
				sb.append(String.format("|   %-26s %2d %6.2f |\n",  extra.getName(), extra.getQuantity(), extra.getUnitPrice()));
				total += extra.getQuantity() * extra.getUnitPrice();
				discount += extra.getQuantity() * extra.getDiscount();
			}
			discount += product.getQuantity() * product.getDiscount();
		}
		total -= discount;
		
		NumberFormat nf = NumberFormat.getCurrencyInstance();
		String totalStr = nf.format(total);
		
		sb.append("|                                        |\n");
		sb.append(String.format("| %-31s %6.2f |\n", "Discount", discount));
		sb.append(String.format("| %-5s %32s |\n", "Total", totalStr));
		sb.append("|                                        |\n");
		sb.append("|----------------------------------------|\n");
		sb.append("|                                        |\n");
		for (String line : systemService.getInvoiceFooter()) {
			sb.append(String.format("|%40s|\n", padStr(line.trim(), pageWidth, ' ')));
		}
		sb.append("|                                        |\n");
		sb.append("+----------------------------------------+\n");
		return sb.toString();
	}

}
