package com.ultrahook.dtos;

import java.util.Map;

public class UltrahookMessage {
	private String type; 
	private String body; 
	private String query; 
	private String path; 
	private Map<String,String> headers;
	private UltrahookMessage(String type, String body, String query,
			String path, Map<String, String> headers) {
		super();
		this.type = type;
		this.body = body;
		this.query = query;
		this.path = path;
		this.headers = headers;
	}
	public String getType() {
		return type;
	}
	public String getBody() {
		return body;
	}
	public String getQuery() {
		return query;
	}
	public String getPath() {
		return path;
	}
	public Map<String, String> getHeaders() {
		return headers;
	}
	
}
