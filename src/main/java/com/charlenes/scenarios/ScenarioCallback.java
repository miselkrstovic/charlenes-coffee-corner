package com.charlenes.scenarios;

import java.util.List;

import com.charlenes.models.Order;

public interface ScenarioCallback {	
	void onBeforeOrdersChange(List<Order> orders);
	void onAfterOrdersChange(List<Order> orders);
	void onBeforeCurrentOrderChange(Order order);
	void onAfterCurrentOrderChange(Order order);
}
