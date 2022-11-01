package com.charlenes.services;

public class NoSuchOrderException extends Exception {

	public NoSuchOrderException() {
		super();
	}
	
	public NoSuchOrderException(String message) {
		super(message);
	}
	
}
