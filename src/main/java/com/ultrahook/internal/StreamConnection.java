package com.ultrahook.internal;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class StreamConnection {
	private String url;
	private StreamProcessor processor;
	private int timeout = 60000;
	private CloseableHttpResponse response;
	private volatile boolean stopped = false; 
	public StreamConnection(String url, StreamProcessor processor) {
		super();
		this.url = url;
		this.processor = processor;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public void start() throws IOException {
		stopped = false; 
		new Thread() {
			@Override
			public void run() {
				try {
					while (!stopped) {
						RequestConfig config = RequestConfig.custom()
								.setSocketTimeout(timeout).build();
						CloseableHttpClient client = HttpClients.custom()
								.setDefaultRequestConfig(config).build();
						HttpGet get = new HttpGet(url);
						response = client.execute(get);
						InputStream stream = response.getEntity().getContent();
						processor.process(stream);						
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}.start();
	}

	public void stop() throws IOException {
		stopped = true; 
		response.close();
	}

}
