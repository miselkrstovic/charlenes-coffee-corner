package com.charlenes.models;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class Entity {

	private static Map<String, Long> _counter = new HashMap<>();

	public synchronized Long getNextId() {
		String className = this.getClass().getSimpleName();
		Long nextId;
		if (_counter.containsKey(className)) {
			nextId = _counter.get(className).longValue() + 1;
			_counter.replace(className, nextId);	
		} else {
			nextId = 1L;
			_counter.put(className, nextId);
		}
		return nextId;
	}
	
	public String getNextUuid() {
		return UUID.randomUUID().toString();
	}
	
}
