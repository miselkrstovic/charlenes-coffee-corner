package com.charlenes.scenarios;

import java.util.List;
import java.util.logging.Logger;

import com.charlenes.models.Extra;
import com.charlenes.models.Order;
import com.charlenes.models.Product;
import com.charlenes.models.ProductType;

public class ScenarioFactory {

	private static Logger logger = Logger.getLogger(ScenarioFactory.class.getSimpleName());
	
	private ScenarioFactory() {}
	
	public static ScenarioCallback getStandardScenario() {
		return new ScenarioCallback() {
		
			@Override
			public void onBeforeOrdersChange(List<Order> orders) {
				logger.info("Scenario before orders history change");
			}
			
			@Override
			public void onAfterOrdersChange(List<Order> orders) {
				logger.info("Scenario after orders history change");

				// Recent scenario cases
				if (orders.size() == 1) {
					Order currentOrder = orders.get(orders.size() - 1);
					
					updateExtraPricesOnBeverageAndSnackPurchases(currentOrder);
					updateEveryFifthDrinkIsFree(currentOrder);
				}
				
				// History based scenario cases
				for (Order order : orders) {

				}
			}

			@Override
			public void onBeforeCurrentOrderChange(Order order) {
				logger.info("Scenario after current order items change");
			}

			@Override
			public void onAfterCurrentOrderChange(Order order) {
				logger.info("Scenario after current order items change");
			}
			
			private void updateEveryFifthDrinkIsFree(Order order) {
				int beverageCount = 0;
				
				for (Product product : order.getProducts()) {
					if (product.getType() ==  ProductType.BEVERAGE) {
						product.setDiscount(0.0);
						
						for (int i = 1; i <= product.getQuantity(); i++) {							
							beverageCount++;
							
							if (beverageCount % 5 == 0) {
								product.setDiscount(product.getUnitPrice());
							}
						}
					}
				}
			}
			
			private void updateExtraPricesOnBeverageAndSnackPurchases(Order order) {
				int beverageCount = 0;
				int snackCount = 0;
				int freeExtraCount = 0;
				
				for (Product product : order.getProducts()) {
					if (product.getType() ==  ProductType.BEVERAGE) {
						beverageCount++;
					}
					if (product.getType() ==  ProductType.SNACK) {
						snackCount++;
					}		
				}
				if (snackCount == beverageCount && snackCount > 0) {
					freeExtraCount = snackCount;
				} else {
					freeExtraCount = Math.max(snackCount, beverageCount) - Math.abs(snackCount - beverageCount);
				}
				
				if (freeExtraCount > 0) {
					for (Product product : order.getProducts()) {
						for (Extra extra : product.getExtras()) {
							extra.setDiscount(0.0);
							
							if (freeExtraCount > 0) {
								extra.setDiscount(extra.getUnitPrice());
								freeExtraCount--;
							}
							
							if (freeExtraCount == 0) break;
						}	
						if (freeExtraCount == 0) break;
					}	
				}			
			}

		};
	}

}
