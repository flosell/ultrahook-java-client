package com.ultrahook.dtos;

/**
 * @author florian
 *
 */
public class InitResponse {
	private boolean success; 
	private String url; 
	private String namespace;
	private String error; 
	
	private InitResponse(boolean success, String url, String namespace,
			String error) {
		super();
		this.success = success;
		this.url = url;
		this.namespace = namespace;
		this.error = error;
	}

	public String getError() {
		return error;
	}
	
	public boolean isSuccess() {
		return success;
	}
	public String getUrl() {
		return url;
	}
	public String getNamespace() {
		return namespace;
	}

	@Override
	public String toString() {
		return "InitResponse [success=" + success + ", url=" + url
				+ ", namespace=" + namespace + ", error=" + error + "]";
	}
	
	
}
