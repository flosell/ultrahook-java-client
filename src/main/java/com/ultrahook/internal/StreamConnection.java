package com.ultrahook.internal;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class StreamConnection {
	private String url;
	private StreamProcessor processor; 
	public StreamConnection(String url, StreamProcessor processor) {
		super();
		this.url = url;
		this.processor=processor; 
	} 
	
	public void start() throws IOException {
		RequestConfig config = RequestConfig.custom().setConnectionRequestTimeout(60000).build(); 
		CloseableHttpClient client = HttpClients.custom().setDefaultRequestConfig(config).build();
		HttpGet get = new HttpGet(url);
		CloseableHttpResponse response = client.execute(get);
		InputStream stream = response.getEntity().getContent();
		processor.process(stream); 
	}
	
}
