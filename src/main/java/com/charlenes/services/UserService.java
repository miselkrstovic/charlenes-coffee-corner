package com.charlenes.services;

import com.charlenes.models.Customer;
import com.charlenes.models.Staff;

public class UserService {

    private static volatile Customer customerInstance = null;
    private static volatile Staff staffInstance = null;
    
    public UserService() {}

	public Customer getCustomerSingleton(String name, String description) {
           if (customerInstance == null) {
                synchronized (Customer.class) {
                        if (customerInstance == null) {
                        	customerInstance = new Customer(name, description);
                        }
                }
           }
           return customerInstance;
    }
	    
    public Staff getEmployeeSingleton(String name, String description) {
           if (staffInstance == null) {
                synchronized (Staff.class) {
                        if (staffInstance == null) {
                        	staffInstance = new Staff(name, description);
                        }
                }
           }
           return staffInstance;
    }
	
}
