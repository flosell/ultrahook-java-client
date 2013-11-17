package com.ultrahook.test.utils;

public class TestProperties {
	public static String getAPIKey()  {
		String key = System.getProperty("apiKey");
		if (key==null || key.trim().isEmpty()) {
			throw new RuntimeException("no apiKey defined for tests"); 
		}
		return key; 
//		return ""; // FIXME
	}
}
